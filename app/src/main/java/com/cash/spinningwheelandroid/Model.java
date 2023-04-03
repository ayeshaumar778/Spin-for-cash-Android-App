package com.cash.spinningwheelandroid;

/**
 * Created by Hetal on 02-Apr-18.
 */

public class Model {
    String desc,date,coin,status;

    public Model() {
    }

    public Model(String desc, String date, String coin,String status) {
        this.desc = desc;
        this.date = date;
        this.coin = coin;
        this.status=status;
    }

    public String getDesc() {
        return desc;
    }

    public String getDate() {
        return date;
    }

    public String getCoin() {
        return coin;
    }

    public String getStatus() {
        return status;
    }
}

