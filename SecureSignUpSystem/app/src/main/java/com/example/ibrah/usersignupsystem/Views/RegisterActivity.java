package com.example.ibrah.usersignupsystem.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ibrah.usersignupsystem.R;

import com.example.ibrah.usersignupsystem.Models.Login.User;
import com.example.ibrah.usersignupsystem.Models.Hash192.MyHash;
import com.example.ibrah.usersignupsystem.Controllers.Encode;
import com.example.ibrah.usersignupsystem.Controllers.Login.UserRules;
import com.example.ibrah.usersignupsystem.Views.Login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;

//Bu aktivite kullanici kayit aktivitesidir.

public class RegisterActivity extends Activity {

    private EditText et_userName,et_email,et_password,et_passwordConfirm;                 //veri girisi yapilan editText'ler
    private TextView tv_error,err_userName,err_email,err_password,err_passwordConfirm;    //tv_error = hata mesaji, err_* = ilgili verinin yanlis oldugunu gosteren isaret.
    private final DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
    private final DatabaseReference salts = FirebaseDatabase.getInstance().getReference("Salt");
    private ValueEventListener listenerUser;
    private User new_user;
    private String passwordConfirm;

    private void init(){

        et_userName = findViewById(R.id.reg_userName);
        et_email = findViewById(R.id.reg_email);
        et_password = findViewById(R.id.reg_password);
        et_passwordConfirm = findViewById(R.id.reg_passwordConfirm);
        tv_error = findViewById(R.id.reg_error);

        err_userName = findViewById(R.id.reg_userName_err);
        err_email = findViewById(R.id.reg_email_err);
        err_password = findViewById(R.id.reg_password_err);
        err_passwordConfirm = findViewById(R.id.reg_passwordConfirm_err);

        listenerUser = new ValueEventListener() {       //veri tabanı dinleyicisi
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {                                               //kayit olmak istenilen email adresin zaten kullanilmis olma durumu
                    tv_error.setText(R.string.error_registered_email);
                    err_email.setVisibility(View.VISIBLE);
                }
                else {

                    String salt = createRandomSalt();           //rastgele 48 karakter tuz uretiyoruz
                    MyHash myHash = new MyHash();

                    //Asagida once parolanin hashini aliyoruz, sonra tuzu ile hashlenmis parolaya ekliyoruz, sonra birlestirilmis bu verinin bir daha hash'ini aliyoruz
                    //Bu bizim veri tabaninda gorunecek parolamiz oluyor
                    new_user.setPassword(myHash.hash(myHash.hash(new_user.getPassword())+salt));

                    //veritabanina kayit islemleri
                    users.child(Encode.encode(new_user.getEmail())).child("email").setValue(new_user.getEmail());
                    users.child(Encode.encode(new_user.getEmail())).child("userName").setValue(new_user.getUserName());
                    users.child(Encode.encode(new_user.getEmail())).child("password").setValue(new_user.getPassword());                     //kullaniciyi veri tabanina kayit etme
                    salts.child(Encode.encode(new_user.getEmail())).setValue(salt);                                             //tuzu da ekliyoruz veri tabanimiza (kendi tablosuna)

                    users.child(Encode.encode(new_user.getEmail())).removeEventListener(listenerUser);                      //isimiz bittiten sonra dinleyiciyi silip giris ekranina geri donuyoruz
                    Toast.makeText(RegisterActivity.this,"Kayıt Başarılı",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                tv_error.setText(R.string.error_database_read);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.register_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id==R.id.profile){

            return true;
        }
        if(id==R.id.setting){

            return true;
        }
        return true;
    }
    public void reg_register_onclick(View view) {       //kayit Ol tusu tiklama olayi

        passwordConfirm = String.valueOf(et_passwordConfirm.getText());
        new_user = new User(String.valueOf(et_userName.getText()),String.valueOf(et_email.getText()),String.valueOf(et_password.getText()));

        if(ruleChecker()){

            tv_error.setText("");
            users.child(Encode.encode(new_user.getEmail())).addValueEventListener(listenerUser);  //emaile girilen degere ait veritabanındaki referansa giris kosullarini iceren listener'ı atıyoruz. email yoksa null donuyor
        }
    }

    //rastgele tuz üretimi; 16'lik sistemde 48 karakter (farkli da olabilirdi, hash ciktim ile ayni olsun istedim)
    private String createRandomSalt() {

        Random rand = new Random();
        String salt = "";

        for(int i=0; i<48; i++)
                salt += Integer.toHexString(rand.nextInt(16));

        return salt;
    }


    private boolean ruleChecker() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean result = true;

        if(cm.getActiveNetworkInfo() == null) {

            tv_error.setText(R.string.error_network);
            result = false;
        }
        else{

            err_userName.setVisibility(View.INVISIBLE);
            err_email.setVisibility(View.INVISIBLE);
            err_password.setVisibility(View.INVISIBLE);
            err_passwordConfirm.setVisibility(View.INVISIBLE);


            if(new_user.getPassword() == null || new_user.getPassword().equals("")) {                   //sifrenin bos olma durumu

                tv_error.setText(getString(R.string.error_no_password));
                err_password.setVisibility(View.VISIBLE);
                err_passwordConfirm.setVisibility(View.VISIBLE);
                result = false;
            }
            else {

                if(!UserRules.check_password(new_user.getPassword())){                        //sifrenin kurallara uymama durumu
                    tv_error.setText(getString(R.string.error_invalid_password));
                    err_password.setVisibility(View.VISIBLE);
                    result = false;
                }
                if(!new_user.getPassword().equals(passwordConfirm)) {     //parola ve parolaonaylanin farkli olma durumu
                    tv_error.setText(R.string.error_wrong_passwordConfirm);
                    err_passwordConfirm.setVisibility(View.VISIBLE);
                    err_password.setVisibility(View.VISIBLE);
                    result = false;
                }
            }
            if(!UserRules.check_email(new_user.getEmail())) {                                 //girilen mail adresin desteklenmemesi olma durumu
                tv_error.setText(R.string.error_invalid_email);
                err_email.setVisibility(View.VISIBLE);
                result = false;
            }
            if(!UserRules.check_name(new_user.getUserName())) {                                         //kullanici adinin kurallara uymama durumu
                tv_error.setText(R.string.error_invalid_username);
                err_userName.setVisibility(View.VISIBLE);
            }
        }
        return result;
    }

    public void reg_cancel_onclick(View view) {     //iptal tusu tiklama olayi, giris sayfasina dönme islemi

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
