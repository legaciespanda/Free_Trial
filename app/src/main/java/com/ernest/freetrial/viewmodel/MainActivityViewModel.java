package com.ernest.freetrial.viewmodel;

import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final static String TRIALY_APP_KEY = "N1PG28SX7DMMTIXHJ6G";
    private final static String TRIALY_SKU = "default";


    public String getTrialyAppKey(){
        return TRIALY_APP_KEY;
    }

    public String getTrialySku(){
        return TRIALY_SKU;
    }


}
