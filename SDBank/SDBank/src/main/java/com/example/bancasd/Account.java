package com.example.bancasd;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String id, name, surname;
    private List<Movement> movements;
    private List<Transaction> transactions;
    private float balance;

    public Account(String name, String surname) {
        this.id = UUIDv4.getUUID();
        this.name = name;
        this.surname = surname;
        this.balance = 0;
        this.movements = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public Account(String id, String name, String surname, float balance) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.balance = balance;
        this.movements = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }

    public void addMovement(Movement movement) {
        this.movements.add(movement);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance += balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public String toStringWithMovementId() {
        String accountInformation = "id: " + this.id + "\n" + this.name + " " + this.surname + "\nbalance:" + this.balance;
        String movementInformation = "";
        String transactionInformation = "";
        for (Movement m : movements) {
            movementInformation += m.getId() + "\n";
        }
        for (Transaction t : transactions) {
            transactionInformation += t.getId() + "\n";
        }
        return accountInformation + "\nLista id movimenti\n" + movementInformation+ "\nLista id transazioni\n"+transactionInformation;
    }

    @Override
    public String toString() {
        String accountInformation = "id: " + this.id + "\n" + this.name + " " + this.surname + "\nbalance:" + this.balance;
        String movementInformation = "";
        return accountInformation + "\nLista id movimenti\n" + movementInformation;
    }
}
