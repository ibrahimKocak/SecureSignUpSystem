package com.example.ibrah.usersignupsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Bu aktivite kullanici giris aktivitesidir.

public class LoginActivity extends Activity {

    private EditText et_email,et_password;
    private TextView tv_error,err_email,err_password;
    private String email,password;
    private ValueEventListener listenerUser,listenerSalt;
    private String salt = "";
    private final DatabaseReference users = FirebaseDatabase.getInstance().getReference("User");
    private final DatabaseReference salts = FirebaseDatabase.getInstance().getReference("Salt");

    void init(){

        et_email = findViewById(R.id.login_email);
        et_password = findViewById(R.id.login_password);
        tv_error = findViewById(R.id.login_error);
        err_email = findViewById(R.id.login_email_err);
        err_password = findViewById(R.id.login_password_err);

        final MyHash myHash = new MyHash();

        // Read salt from the database
        listenerSalt = new ValueEventListener() {       //veri tabanı dinleyicisi
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                    salt = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                tv_error.setText(R.string.error_database_read);
            }
        };

        // Read user from the database
        listenerUser = new ValueEventListener() {       //veri tabanı dinleyicisi
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String saltedHashedPassword = myHash.hash(myHash.hash(password)+salt);

                if(!dataSnapshot.exists() || !dataSnapshot.child("password").exists() || !dataSnapshot.child("password").getValue().equals(saltedHashedPassword) || salt.equals("")) {                                            //login_onclick ile editboxtan gelen verinin veri tabaninda olmama durumu
                    tv_error.setText(R.string.error_wrong_password);
                    err_email.setVisibility(View.VISIBLE);
                    err_password.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(LoginActivity.this,"Giriş Başarılı",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
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
        setContentView(R.layout.activity_login);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.login_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id==R.id.setting){

            return true;
        }
        if(id==R.id.about){

            return true;
        }
        return true;
    }

    public void login_login_onclick(View view) {

        email = String.valueOf(et_email.getText());
        password = String.valueOf(et_password.getText());

        if(ruleChecker()){

            tv_error.setText("");
            salts.child(Encode.encode(email)).addValueEventListener(listenerSalt);
            users.child(Encode.encode(email)).addValueEventListener(listenerUser);  //emaile girilen degere ait veritabanındaki referansa giris kosullarini iceren listener'ı atıyoruz. email yoksa null donuyor
        }
    }

    private boolean ruleChecker() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean result = true;

        if(cm.getActiveNetworkInfo() == null) {

            tv_error.setText(R.string.error_network);
            result = false;
        }
        else{

            err_email.setVisibility(View.INVISIBLE);
            err_password.setVisibility(View.INVISIBLE);

            if(password == null || password.equals("")) {                   //sifrenin bos olma durumu

                tv_error.setText(getString(R.string.error_no_password));
                err_password.setVisibility(View.VISIBLE);
                result = false;
            }
            else if(!UserRules.check_password(password)){                        //sifrenin kurallara uymama durumu (kurala uymuyorsa veri tabanına gitmesine gerek yok)
                tv_error.setText(getString(R.string.error_wrong_password));
                err_password.setVisibility(View.VISIBLE);
                err_email.setVisibility(View.VISIBLE);
                result = false;
            }
            if(!UserRules.check_email(email)) {                                 //girilen mail adresin desteklenmemesi olma durumu
                tv_error.setText(R.string.error_invalid_email);
                err_email.setVisibility(View.VISIBLE);
                err_password.setVisibility(View.VISIBLE);
                result = false;
            }
        }
        return result;
    }

    public void login_sign_onclick(View view) {

        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public void login_cancel_onclick(View view) {

        finish();
    }
}
