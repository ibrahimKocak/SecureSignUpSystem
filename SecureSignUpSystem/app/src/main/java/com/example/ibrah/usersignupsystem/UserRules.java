package com.example.ibrah.usersignupsystem;

//Bu sinif kullanici verilerinin bazi kurallara uyma durumunu kontrol eder.

class UserRules {

    static boolean check_name(String userName) {

        return !(userName.length() < 6 || userName.length() > 20);
    }

    static boolean check_email(String email) {

        return email.endsWith("@hotmail.com") ||       //mail kisminin populer bir email servisi olarak girilmesini istiyoruz
                email.endsWith("@gmail.com") ||
                email.endsWith("@outlook.com") ||
                email.endsWith("@outlook.com.tr") ||
                email.endsWith("@yahoo.com") ||
                email.endsWith("@yahoo.com.tr") ||
                email.endsWith("@mail.com") ||
                email.endsWith("@mail.com.tr") ||
                email.endsWith("@yandex.com") ||
                email.endsWith("@yandex.com.tr") ||
                email.endsWith("@mynet.com") ||
                email.endsWith("@mynet.com.tr") ||
                email.endsWith("@windowslive.com");
    }

    static boolean check_password(String password) {        //sifre 8 ile 16 karakter arasinda olsun istiyoruz

        return !(password.length() < 8 || password.length() > 16);
    }
}
