package com.Amogh.url_shortener.util;

public class Base62Encoder {

    private static final String ALPHABET="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encode(long id){
        if(id<0){
            throw new IllegalArgumentException("id cannot be negative "+id);
        }
        if(id==0){
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder sb=new StringBuilder();

        while(id>0){
            long rem=id%62;
            id=id/62;
            sb.append(ALPHABET.charAt((int)rem));

        }
        return sb.reverse().toString();





    }

    public static long decode(String shortCode){
        long ans=0;
        for(int i=0;i<shortCode.length();i++){
            int ind=ALPHABET.indexOf(shortCode.charAt(i));
            ans=ans*62+ind;
        }
        return  ans;
    }
    public static  boolean isValidBase62(String shortCode) {
        if(shortCode==null || shortCode.isEmpty()){
            return false;
        }

        for(int i=0;i<shortCode.length();i++){
            if(ALPHABET.indexOf(shortCode.charAt((i)))==-1){
                return false;
            }


        }
        return true;

    }

    public static void main(String[] args) {
        System.out.println(encode(100));  // 1C
        System.out.println(encode(0));    // 0
        try {
            encode(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }






}
