package com.example.ibrah.usersignupsystem.Controllers;

//Google firebase veritabani referans olarak nokta (.) isaretine izin vermediği ve
//referansa nokta (.) iceren email adresleri kayit etmek istediğim icin encode, decode islemleri yapiyorum.

public class Encode {

    public static String encode(String s) {

        return s.replace(".", "☻");
    }

    static String decode(String s) {

        return s.replace("☻", ".");
    }
}
