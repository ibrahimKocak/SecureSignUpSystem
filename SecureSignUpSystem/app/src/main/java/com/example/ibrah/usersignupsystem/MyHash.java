package com.example.ibrah.usersignupsystem;

public class MyHash {

    //32 bitlik son block ile xor işlemine girecek tekrarsız rastgele sayilar ile olusturulmus k dizisi, her tur biri kullanilacak (96 tur, 96 sayi)
    private int[] k = {1551964966, -1674516431, -1600653260, 1015647129, -359448219, 142307339, -1020458927, 754825710,
            -2028551839, -105848627, -1741439849, 2008096230, 1577899752, -519720826, -537703959, -857551980, 175242116,
            -1695761009, -1690186627, 1690654224, 361881860, -652695932, -1565387954, 356531726, -1508387594, 1910003838,
            1035387259, 849492032, -2112544035, -1194795534, 1731709920, 852648893, -615720153, -1241765114, 1477957309,
            1227910162, 1231310146, 699273303, 1692089660, -1265362422, -863479578, 283932705, 1035250053, 1814145085,
            -886900011, -1696107401, 819035137, -284641184, -1069807865, 16004597, -225311411, -606688986, -2070838296,
            -1702525420, -107724071, 265513673, 1767396296, -1613270694, -871463662, 1136759548, -1834961139, 802862592,
            659057518, 1197536063, 1778480847, -505578285, -1840251316, -1374781599, 1772302645, -1798399305, 1265356647,
            -248588665, -1688797256, 207085528, 1063898815, -208049281, -961810267, 633761654, 826869751, -1367165095,
            -334035833, -87191247, 2055493528, -1351983033, -1870778435, -499902894, 627536653, 251663435, -1057617990,
            1701807347, 1640816190, -1793579980, 452748357, -34591231, 104898579, -1832716584};

    String hash(String value){

        ConvertBlocks192 convertBlocks192 = new ConvertBlocks192();         //hash'lenecek veriyi integer blocklari haline cevirecek nesene

        int[][] blocks192;

        if(value == null || value.equals(""))                            //hash'lenecek veri null veya bos ise byte'a cerilmiyor, bizde bosluk geri atarak bu durmdan kurtuluyoruz
            blocks192 = convertBlocks192.convert(" ");                 //ve sayi bloklarini olusturuyoruz. her biri 6 adet 32 bitlik veri tutan 192 bitlik bloklar
        else
            blocks192 = convertBlocks192.convert(value);

        int[] temp192;                                      //var ise bir onceki 192bitlik blogun tutulacagi yedek blok
        int temp32;                                     //32bitlik bloklari kaydirma isleminde kullanilacak olan yedek

        for (int i = 0; i < blocks192.length; i++) {    //her 192bitlik block icin...

            if(i != 0)                                //192btlik blogumuz ilk degil ise yedege atiyoruz, hash isleminde kullanilacak
                temp192 = blocks192[i-1];
            else
                temp192 = blocks192[i];             //ilk ise kendisini kullanacagiz.

            for (int j = 0; j < 96; j++) {          //96 tur islemden gecirecegiz, her 24 tur da kullanilan fonksiyon degisecek

                temp32 = blocks192[i][2];                               //her islem sonunda 32 bitlik bloklari dairesel olarak cevirecegimiz icin birini yedek aliyoruz. 3. nün orjinali de lazim o yüzden 2 yi tedekliyoruz.
                blocks192[i][2] = shift(blocks192[i][2], 19);             //3. elemanib bitlerini dairesel olarak 19 kere sola kaydiriyoruz

                //fonsiyonlarimiz, ilgili fonksiyonislenen 32bitlik bloklar ise son 32bitlik blogu xor islemi yaptirip son bloga yaziyoruz.
                if (i < 24)
                    blocks192[i][5] ^= f1(blocks192[i][1], blocks192[i][2], blocks192[i][3], blocks192[i][4]);
                else if (i < 48)
                    blocks192[i][5] ^= f2(blocks192[i][1], blocks192[i][2], blocks192[i][3], blocks192[i][4]);
                else if (i < 72)
                    blocks192[i][5] ^= f3(blocks192[i][1], blocks192[i][2], blocks192[i][3], blocks192[i][4]);
                else
                    blocks192[i][5] ^= f4(blocks192[i][1], blocks192[i][2], blocks192[i][3], blocks192[i][4]);


                //32 bitlik blocklar arasi islemler..
                blocks192[i][5] ^= temp192[j%6];                            //eski 192bitlik blogun islenmesi sonucu cikan veri ile xor islemi (6 adet 32 bit var o yuzden %6)
                blocks192[i][0] = shift(blocks192[i][0], 7);            //ilk 32 bitlik blogu dairesel olarak 7 kere sola kaydiriyoruz
                blocks192[i][5] ^= blocks192[i][0];                         //son blok ile ilk blogu xor islemi yaptirip son bloga atiyoruz
                blocks192[i][5] ^= k[i];                                //son blogu sabit kdizisinin ilgi elemani ile xor luyoruz.


                //bu kisimda 6 adet 32 bitlik blocu saga dogru 1 kere kaydiriyoruz.
                blocks192[i][2] = blocks192[i][1];
                blocks192[i][1] = shift(blocks192[i][0],3);
                blocks192[i][0] = blocks192[i][5];
                blocks192[i][5] = blocks192[i][4];
                blocks192[i][4] = blocks192[i][3];
                blocks192[i][3] = temp32;
            }
        }

        String result = "";

        //elde edilen veriyi 16lik tabana (hexedecimal) cevirip bir string'e ekliyoruz
        for(int i=0; i < blocks192[0].length; i++)
            result = result + String.format("%8s", (Integer.toHexString(blocks192[blocks192.length - 1][i]))).replace(' ', '0');

        return result;
    }

    //32 bitlik blocklarrın bitlerinin sola dairesel kaydirma islemi
    private int shift(int A, int count){

        return Integer.rotateLeft(A, count);
/*
        int temp = A ;
        temp >>>= 30-count;
        A = A <<= count;
        A += temp;
        return A;
*/
    }


    //her 24 turda değişecek olan fonksiyonlar
    private int f1(int B, int C, int D, int E){

        return (B & C) | (D & E);
    }
    private int f2(int B, int C, int D, int E){

        return (B | C) | (~D ^ E);
    }
    private int f3(int B, int C, int D, int E){

        return (B | D) ^ (C & E);
    }
    private int f4(int B, int C, int D, int E){

        return (B ^ ~C) ^ (D | E);
    }
}
