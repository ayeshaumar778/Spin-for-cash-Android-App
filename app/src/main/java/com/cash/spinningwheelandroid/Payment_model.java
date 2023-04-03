package com.cash.spinningwheelandroid;

/**
 * Created by Hetal on 03-Apr-18.
 */

public class Payment_model {
    String rs,method,account,date;

    public Payment_model() {
    }



    public Payment_model(String rs, String method, String account, String date) {
        this.rs = rs;
        this.method = method;
        this.account = account;
        this.date=date;

    }

    public String getDate() {
        return date;
    }

    public String getRs() {
        return rs;
    }

    public String getMethod() {
        return method;
    }

    public String getAccount() {
        return account;
    }
}
