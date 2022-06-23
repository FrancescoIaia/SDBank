package com.example.bancasd;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


@RestController
public class ManageAPI {
    private final ManagerDB MDB = new ManagerDB();
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public Map<String, String> parseBody(String str) {
        Map<String, String> body = new HashMap<>();
        String[] values = str.split("&");
        for (String value : values) {
            String[] coppia = value.split("=");
            if (coppia.length != 2) {
                continue;
            } else {
                body.put(coppia[0], coppia[1]);
            }
        }
        return body;
    }


    @RequestMapping(value = "/api/account/", method = RequestMethod.GET)
    public List<MiniAccount> getAccount() {
        List<MiniAccount> list = new ArrayList<>();
        for (Account account : MDB.getAllAccountFromDB()) {
            list.add(new MiniAccount(account.getId(), account.getName(), account.getSurname(), account.getBalance()));
        }
        return list;
    }

    @RequestMapping(value = "/api/account/", method = RequestMethod.POST)
    public ResponseEntity<String> addAccount(@RequestBody String newAccount) {
        Map<String, String> body = parseBody(newAccount);
        Account a = new Account(body.get("name"), body.get("surname"));
        if (a.getName() == null || a.getSurname() == null) {
            String json = new JSONObject()
                    .put("Errore", "Inserito un valore errato")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        if (MDB.addAccount(a)) {
            String json = new JSONObject()
                    .put("id", a.getId())
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.CREATED);
        } else {
            String json = new JSONObject()
                    .put("Errore", "failed")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/account/", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAccount(@RequestParam String idAccount) {
        Account account = MDB.getAccountWithId(idAccount);
        if (account != null) {
            MDB.deleteAccount(idAccount);
            String json = new JSONObject()
                    .put("account", idAccount)
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        } else {
            String json = new JSONObject()
                    .put("errore", "L'account non esiste")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/api/account/{accountId}/", method = RequestMethod.GET)
    public HttpEntity<String> getAccount(@PathVariable String accountId) throws JsonProcessingException {
        HttpEntity<String> response;
        Account a = MDB.getAccountWithId(accountId);

        if (a != null) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("X-Sistema-Bancario", a.getName() + ";" + a.getSurname());
            String json = ow.writeValueAsString(a);
            return new HttpEntity<>(json, map);
        } else {
            String json = new JSONObject()
                    .put("Errore", "L'account non esiste")
                    .toString();
            response = new HttpEntity<>(json);
            return response;
        }

    }

    @RequestMapping(value = "/api/detailedAccount/{accountId}/", method = RequestMethod.GET)
    public ResponseEntity<String> getdetailedAccount(@PathVariable String accountId) throws JsonProcessingException {
        Account account = MDB.getAccountWithId(accountId);
        if (account == null) {
            String json = new JSONObject()
                    .put("Errore", "L'account non esiste")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        String json = ow.writeValueAsString(account);
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/account/{accountId}/", method = RequestMethod.HEAD)
    public ResponseEntity<String> getHeadAccount(@PathVariable String accountId) {
        ResponseEntity<String> response;
        Account a = MDB.getAccountWithId(accountId);

        if (a != null) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("X-Sistema-Bancario", a.getName() + ";" + a.getSurname());
            response = new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            String json = new JSONObject()
                    .put("Errore", "L'account non esiste")
                    .toString();
            response = new ResponseEntity<>("Account Doesn't Exist", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping(value = "/api/account/{accountId}/", method = RequestMethod.POST)
    public ResponseEntity<String> addMovement(@RequestBody String amountBody, @PathVariable String accountId) throws JsonProcessingException {
        Map<String, String> body = parseBody(amountBody);
        float amount;
        try {
            amount = Float.parseFloat(body.get("amount"));

        } catch (NumberFormatException e) {
            String json = new JSONObject()
                    .put("Errore", "Inserire Un Numero ")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        Account acc = MDB.getAccountWithId(accountId);

        if (acc == null) {
            String json = new JSONObject()
                    .put("Errore", "L'utente non esiste")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }

        if (acc.getBalance() + amount < 0) {
            String json = new JSONObject()
                    .put("Errore", "Il saldo è inferiore alla transazione")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }

        Movement m = new Movement(amount);
        acc.setBalance(amount);
        MDB.setBalance(acc.getBalance(), accountId);
        MDB.createMovements(accountId, amount, m.getType().name(), m.getId());
        String json = new JSONObject()
                .put("balance", acc.getBalance())
                .put("id", m.getId())
                .toString();

        return new ResponseEntity<>(json, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/account/{accountId}/", method = RequestMethod.PUT)
    public ResponseEntity<String> updateAccount(@PathVariable String accountId, @RequestBody String newProprietaryBody) {
        Map<String, String> body = parseBody(newProprietaryBody);
        String name = body.get("name");
        String surname = body.get("surname");
        if (name == null || surname == null) {
            String json = new JSONObject()
                    .put("Errore", "Dati errati")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        if (MDB.getAccountWithId(accountId) == null) {
            String json = new JSONObject()
                    .put("Errore", "L'account non esiste")
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }

        MDB.modifyAccount(name, surname, accountId);
        String json = new JSONObject()
                .put("Messaggio", "Account aggiornato")
                .toString();
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/account/{accountId}/", method = RequestMethod.PATCH)
    public ResponseEntity<String> updateHalfAccount(@PathVariable String accountId, @RequestBody String newProprietaryBody) {
        Map<String, String> body = parseBody(newProprietaryBody);
        String name = body.get("name");
        String surname = body.get("surname");
        if (name != null && surname != null || name == null && surname == null) {
            String json = new JSONObject()
                    .put("Errore", "Specificare o name o surname").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        if (MDB.getAccountWithId(accountId) == null) {
            String json = new JSONObject()
                    .put("Errore", "account inesistente").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        MDB.modifyAccount(name, surname, accountId);
        String json = new JSONObject()
                .put("Messaggio", "account aggiornato").toString();
        return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/api/transfer", method = RequestMethod.POST)
    public ResponseEntity<String> addTransaction(@RequestBody String transactionBody) {
        Account sender = null, receiver = null;
        Map<String, String> body = parseBody(transactionBody);
        float amount = Float.parseFloat(body.get("amount"));
        String senderId = body.get("from"), receiverId = body.get("to");
        sender = MDB.getAccountWithId(senderId);
        if (sender == null) {
            String json = new JSONObject()
                    .put("Errore", "Il Mittente non esiste").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }

        receiver = MDB.getAccountWithId(receiverId);
        if (receiver == null) {
            String json = new JSONObject()
                    .put("Errore", "Il destinatario non esiste").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        if (sender.getBalance() - amount < 0) {
            String json = new JSONObject()
                    .put("Errore", "Il saldo del mittente è inferiore alla cifra della transazione").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        Transaction t = new Transaction(senderId, receiverId, amount);
        if(senderId.equals(receiverId)){
            MDB.createTransaction(t.getId(), senderId, receiverId, amount);
            String json = new JSONObject()
                    .put("Saldo_ID_Mittente", sender.getId())
                    .put("Balance_Mittente", sender.getBalance())
                    .put("Saldo_ID_ricevente", receiver.getId())
                    .put("Balance_ricevente", receiver.getBalance())
                    .put("Transazione_ID", t.getId())
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        }else {
            
            receiver.setBalance(amount);
            sender.setBalance(-amount);

            MDB.setBalance(receiver.getBalance(), receiverId);
            MDB.setBalance(sender.getBalance(), senderId);
            MDB.createTransaction(t.getId(), senderId, receiverId, amount);

            String json = new JSONObject()
                    .put("Saldo_ID_Mittente", sender.getId())
                    .put("Balance_Mittente", sender.getBalance())
                    .put("Saldo_ID_ricevente", receiver.getId())
                    .put("Balance_ricevente", receiver.getBalance())
                    .put("Transazione_ID", t.getId())
                    .toString();
            return new ResponseEntity<>(json, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/api/divert", method = RequestMethod.POST)
    public ResponseEntity<String> reverseTransaction(@RequestBody String transactionBody) {
        Account sender = null, receiver = null;
        Map<String, String> body = parseBody(transactionBody);
        String transID = body.get("id");
        Transaction transaction = MDB.getTransactionID(transID);
        if (transaction == null) {
            String json = new JSONObject()
                    .put("Errore", "La transazione non esiste").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }
        sender = MDB.getAccountWithId(transaction.getReceiver());
        receiver = MDB.getAccountWithId(transaction.getSender());

        if (sender == null) {
            String json = new JSONObject()
                    .put("Errore", "Il Mittente non esiste").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }

        if (receiver == null) {
            String json = new JSONObject()
                    .put("Errore", "Il Destinatario non esiste").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }

        if (sender.getBalance() - transaction.getAmount() < 0) {
            String json = new JSONObject()
                    .put("Errore", "Il saldo del mittente è inferiore alla cifra della transazione").toString();
            return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
        }

        Transaction t = new Transaction(sender.getId(), receiver.getId(), transaction.getAmount());
        receiver.setBalance(transaction.getAmount());
        sender.setBalance(-transaction.getAmount());

        MDB.setBalance(receiver.getBalance(), receiver.getId());
        MDB.setBalance(sender.getBalance(), sender.getId());
        MDB.createTransaction(t.getId(), sender.getId(), receiver.getId(), transaction.getAmount());

        String json = new JSONObject()
                .put("Mittente_ID", sender.getId())
                .put("Mittente_Balance", sender.getBalance())
                .put("Destinatario_ID", receiver.getId())
                .put("Destinatario_Balance", receiver.getBalance())
                .put("transazione_Id", t.getId())
                .toString();

        return new ResponseEntity<>(json, HttpStatus.CREATED);
    }

}
