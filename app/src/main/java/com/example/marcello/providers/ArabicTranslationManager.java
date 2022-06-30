package com.example.marcello.providers;

public class ArabicTranslationManager {

    private static ArabicTranslationManager instance = new ArabicTranslationManager();
    private ArabicTranslationManager(){}
    public static synchronized ArabicTranslationManager getInstance(){
        return instance;
    }
    public void translateToArabic(){

    }
}
