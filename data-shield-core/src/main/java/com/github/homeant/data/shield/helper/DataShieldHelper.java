package com.github.homeant.data.shield.helper;


public class DataShieldHelper {
    private static final ThreadLocal<String> LOCAL_DATA_MASKING = new ThreadLocal();

    public static void dataMasking(String symbol){
        if(symbol==null || symbol.isEmpty()){
            symbol = "*";
        }
        LOCAL_DATA_MASKING.set(symbol);
    }

    public static void dataMasking(){
        dataMasking("*");
    }

    public static String getDataMasking(){
        return LOCAL_DATA_MASKING.get();
    }

    public static void clearDataMasking(){
        LOCAL_DATA_MASKING.remove();
    }
}
