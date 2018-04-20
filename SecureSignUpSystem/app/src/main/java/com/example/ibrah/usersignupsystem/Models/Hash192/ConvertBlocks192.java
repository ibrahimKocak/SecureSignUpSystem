package com.example.ibrah.usersignupsystem.Models.Hash192;

public class ConvertBlocks192 {

    int[][] convert(String s) {

        int[][] block192;

        if(s.equals(""))
            block192 = new int[][]{};
        else
            block192 = convert_block192(convert_block32(getBytesWithPadding(s)));

        return block192;
    }

    private byte[] getBytesWithPadding(String s){

        byte[] bytes = s.getBytes();

        //bytes, s'nin byte karsiligini tutuyor ancak 192 bitlik blocklar icin 192 bit yani 24 bytın katı olsun istiyorum,
        //bytes_new adinda 24 bytin kati olacak sekilde genisletilmis yeni bir dizi olusturup byte dizisini kopyalıyorum
        //kalan kismini zaten default olarak "0" (bit versiyonu 00000000) oluyor. (padding) Sonuc olarak 192 bitin tam kati bir byte dizim oluyor.

        if(bytes.length % 24 != 0){

            byte[] bytes_new = new byte[ bytes.length + (24 - (bytes.length % 24))];

            System.arraycopy(bytes,0,bytes_new,0,bytes.length);

            return bytes_new;
        }
        else
            return bytes;
    }

    private int[] convert_block32(byte[] bytes){

        int block32_length = bytes.length / 4;
        int[] block32 = new int[block32_length];      //8bitlik (1 byte) blocklari 32 bitlik (4 byte) blocklara ceviriyorum

        for(int i=0; i < block32_length; i++){

            for(int j=0; j < 4; j++){

                block32[i] += bytes[i*4+j];         //32 bitlik blokların herbiri 4 adet byte dizisinden bitlerinin birleşmesi şeklinde oluşturuluyor.
                                                    //Bunun icin her byte terimini 32 bitlik terime akleyip 8 bit sola kaydiriyorum. son byte eklenirken yapilmamasi gereken kaydirma islemini yapmiyorum
                if(j != 3)
                    block32[i] <<= 8;
            }
        }

        return block32;
    }

    private int[][] convert_block192(int[] block32){

        //her 192 bitlik blockta 6 adet 32 bitlik block bulunur, getBytesWithPadding ile bitlerin 192 nin kati olmasi saglandi,
        //convert_block32 ile 8 biltik veriler 32 bitlik verilere donustu, Simdi her 6 adet 32 bitlik veriyi 192 bitlik bir dizide
        // parketliyecegiz. bolelikle her 192 bitlik blogu alip 32 bitlik 6 parcasi arasinda islem yapacagiz.

        int block192_length = block32.length / 6;

        int[][] block192 = new int[block192_length][6];

        for(int i=0; i<block192_length;i++)
            for(int j=0; j<6;j++)
                block192[i][j] = block32[i*6+j];

        return block192;
    }
}
