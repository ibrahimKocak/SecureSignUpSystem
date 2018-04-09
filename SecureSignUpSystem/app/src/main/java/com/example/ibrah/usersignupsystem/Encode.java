package com.example.ibrah.usersignupsystem;

//Google firebase veritabani referans olarak nokta (.) isaretine izin vermediği ve
//referansa nokta (.) iceren email adresleri kayit etmek istediğim icin encode, decode islemleri yapiyorum.

class Encode {

    static String encode(String s) {

        return s.replace(".", "☻");
    }

    static String decode(String s) {

        return s.replace("☻", ".");
    }
}
