package com.example.bancasd;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagerDB {
    private Connection c;
    private Statement stmt;

    public ManagerDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.c = DriverManager.getConnection("jdbc:sqlite:bancadb");
            this.c.setAutoCommit(false);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.c = DriverManager.getConnection("jdbc:sqlite:bancadb");
            this.c.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Account> getAllAccountFromDB() {
        List<Account> accounts = new ArrayList<>();
        try {
            this.openConnection();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM account;");
            Account tmp;
            while (rs.next()) {
                String id = rs.getString("id");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                Float balance = rs.getFloat("saldo");
                tmp = new Account(id, nome, cognome, balance);


                accounts.add(tmp);
            }
            c.close();
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addAccount(Account account) {
        try {
            this.openConnection();
            String query = " insert into account (id, nome, cognome, saldo) " +
                    "values (?,?,?,?);";

            PreparedStatement preparedStmt = c.prepareStatement(query);
            preparedStmt.setString(1, account.getId());
            preparedStmt.setString(2, account.getName());
            preparedStmt.setString(3, account.getSurname());
            preparedStmt.setFloat(4, account.getBalance());
            preparedStmt.executeUpdate();
            c.commit();
            c.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteAccount(String id) {
        this.openConnection();

        try {
            String query = " DELETE FROM account WHERE id = ?";
            PreparedStatement preparedStmt = c.prepareStatement(query);
            preparedStmt.setString(1, id);
            preparedStmt.executeUpdate();
            c.commit();
            c.close();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Account getAccountWithId(String idAcc) {
        this.openConnection();

        try {
            String query = "SELECT * FROM account where id = ?;";
            PreparedStatement preparedStmt = c.prepareStatement(query);
            preparedStmt.setString(1, idAcc);
            ResultSet rs = preparedStmt.executeQuery();
            Account account = null;
            while (rs.next()) {
                String id = rs.getString("id");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                Float balance = rs.getFloat("saldo");
                account = new Account(id, nome, cognome, balance);

                String query2 = "select id,data,amount,tipo from movimento where account = ? order by data desc";
                preparedStmt = c.prepareStatement(query2);
                preparedStmt.setString(1, idAcc);
                rs = preparedStmt.executeQuery();
                while (rs.next()) {
                    id = rs.getString("id");
                    String tipo = rs.getString("tipo");
                    balance = rs.getFloat("amount");
                    Date data = rs.getDate("data");
                    Movement movement = new Movement(id, balance, tipo, data);
                    account.addMovement(movement);
                }

                String query3 = "select * from transazione " +
                        "where mittente = ? " +
                        "or destinatario = ?" +
                        "order by data desc;";
                PreparedStatement prp = c.prepareStatement(query3);
                prp.setString(1, idAcc);
                prp.setString(2, idAcc);
                rs = prp.executeQuery();
                while (rs.next()) {
                    id = rs.getString("id");
                    String mittente = rs.getString("mittente");
                    String destinatario = rs.getString("destinatario");
                    balance = rs.getFloat("amount");
                    Date data = rs.getDate("data");
                    System.out.println();
                    Transaction transaction = new Transaction(id, mittente, destinatario, balance, data);
                    account.addTransaction(transaction);
                }
            }
            c.close();

            return account;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBalance(float amount, String accountId) {
        this.openConnection();

        try {
            String query = "update account " +
                    "set saldo = ?" +
                    "where id = ?";
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setFloat(1, amount);
            preparedStatement.setString(2, accountId);
            preparedStatement.executeUpdate();
            c.commit();
            c.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createMovements(String accountId, float amount, String prelievo, String movementId) {
        this.openConnection();

        try {
            String query = "insert into movimento (id, amount, tipo, account)  VALUES (?,?,?,?);";
            PreparedStatement preparedStatement = c.prepareStatement(query);


            preparedStatement.setString(1, movementId);
            preparedStatement.setFloat(2, amount);
            preparedStatement.setString(3, prelievo);
            preparedStatement.setString(4, accountId);

            preparedStatement.executeUpdate();
            c.commit();
            c.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void modifyAccount(String name, String surname, String accountId) {
        this.openConnection();

        try {
            if (name == null && surname != null) {
                String query = "update account " +
                        "set cognome = ?" +
                        "where id = ?;";
                PreparedStatement preparedStatement = c.prepareStatement(query);
                preparedStatement.setString(1, surname);
                preparedStatement.setString(2, accountId);
                preparedStatement.executeUpdate();
                c.commit();
            } else if (name != null && surname == null) {
                String query = "update account " +
                        "set nome = ?" +
                        "where id = ?;";
                PreparedStatement preparedStatement = c.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, accountId);
                preparedStatement.executeUpdate();
                c.commit();
            } else if (name != null && surname != null) {
                String query = "update account " +
                        "set nome = ?, cognome = ?" +
                        "where id = ?;";
                PreparedStatement preparedStatement = c.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surname);
                preparedStatement.setString(3, accountId);
                preparedStatement.executeUpdate();
                c.commit();
            }
            c.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void createTransaction(String idTransaction, String senderId, String reciverId, float amount) {
        this.openConnection();

        try {
            String query = "insert into transazione (id, mittente, destinatario, amount) values (?,?,?,?);";
            PreparedStatement preparedStatement = c.prepareStatement(query);

            preparedStatement.setString(1, idTransaction);
            preparedStatement.setString(2, senderId);
            preparedStatement.setString(3, reciverId);
            preparedStatement.setFloat(4, amount);


            preparedStatement.executeUpdate();
            c.commit();
            c.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Transaction getTransactionID(String idT) {
        this.openConnection();

        try {
            String query = "SELECT * FROM transazione where id = ?;";
            PreparedStatement preparedStmt = c.prepareStatement(query);
            preparedStmt.setString(1, idT);
            ResultSet rs = preparedStmt.executeQuery();
            Transaction transazione = null;
            while (rs.next()) {
                String id = rs.getString("id");
                String mittente = rs.getString("mittente");
                String destinatario = rs.getString("destinatario");
                Float amount = rs.getFloat("amount");
                java.sql.Date data = rs.getDate("data");
                transazione = new Transaction(id, mittente, destinatario, amount, data);
            }
            c.close();

            return transazione;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

