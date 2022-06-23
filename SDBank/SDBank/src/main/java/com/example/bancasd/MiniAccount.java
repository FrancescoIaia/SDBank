package com.example.bancasd;

import java.util.List;

public class MiniAccount {
    private String id, name, surname;
    private float balance;

    public MiniAccount(String id, String name, String surname, float balance) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.balance = balance;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public float getBalance() {
        return balance;
    }
}
