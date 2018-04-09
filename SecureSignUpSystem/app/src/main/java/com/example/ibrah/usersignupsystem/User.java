package com.example.ibrah.usersignupsystem;

//Kullanici sinifi, veri tabanina direk bu sinifin nesneleri kayit edilir.

class User {

    private String email,name,password;

    User() {

    }

    User(String name, String email, String password) {

        this.email = email;
        this.name = name;
        this.password = password;
    }

    String getEmail() {
        return email;
    }
    String getUserName() {
        return name;
    }
    String getPassword() {
        return password;
    }


    //Kullanici degerleri degisirse diye, bu projede kullanilmayacak.
    void setEmail(String email) {
        this.email = email;
    }
    void setUserName(String name) {
        this.name = name;
    }
    void setPassword(String password) {
        this.password = password;
    }
}
