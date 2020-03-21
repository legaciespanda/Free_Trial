package com.ernest.freetrial.viewmodel;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final static String TRIALY_APP_KEY = "OF7TO36SFLIB0F14Y9V";
    private final static String TRIALY_SKU = "com.ernest.freetrial.PREMIUM_FEATURES";


    public String getTrialyAppKey(){
        return TRIALY_APP_KEY;
    }

    public String getTrialySku(){
        return TRIALY_SKU;
    }


}
