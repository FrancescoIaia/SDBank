package com.example.bancasd;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SDBankApplication {

    public static List<Account> accounts;

    public static void main(String[] args) {

        ManagerDB MDB = new ManagerDB();
        SpringApplication.run(SDBankApplication.class, args);
        accounts = MDB.getAllAccountFromDB();
    }
}
