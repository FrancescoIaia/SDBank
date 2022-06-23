package com.example.bancasd;

import java.util.Date;

public class Transaction {
    private String id, sender, receiver;
    private float amount;
    private Date dataTransazione;


    public Transaction(String sender, String receiver, float amount) {
        this.id = UUIDv4.getUUID();
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
    public Transaction(String id, String sender, String receiver, float amount, Date dataTransazione) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.dataTransazione = dataTransazione;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDataTransazione() {
        return dataTransazione;
    }

    public void setDataTransazione(Date dataTransazione) {
        this.dataTransazione = dataTransazione;
    }
}
