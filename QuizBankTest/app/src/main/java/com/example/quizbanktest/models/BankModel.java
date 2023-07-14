package com.example.quizbanktest.models;

public class BankModel {
    String bankName;
    String bankDescription;
    String bankDate;
    int image;

    public BankModel(String bankName, String bankDescription, String bankDate, int image) {
        this.bankName = bankName;
        this.bankDescription = bankDescription;
        this.bankDate = bankDate;
        this.image = image;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankDescription() {
        return bankDescription;
    }

    public String getBankDate() {
        return bankDate;
    }

    public int getImage() {
        return image;
    }
}

