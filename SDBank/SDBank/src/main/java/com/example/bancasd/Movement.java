package com.example.bancasd;

import java.util.Date;

public class Movement {

    public enum Movement_Type {
        payment {
            @Override
            public String toString() {
                return "pagamento";
            }
        }, withdrawal {
            @Override
            public String toString() {
                return "ritiro";
            }
        }
    }

    private String id;
    private float amount;
    private Movement_Type type;
    private Date dataTransazione;

    public Movement(String id, float amount) {
        this.id = id;
        this.amount = amount;
        if (amount > 0) {
            this.type = Movement_Type.payment;
        } else {
            this.type = Movement_Type.withdrawal;
        }
    }

    public Movement(float amount) {
        this.id = UUIDv4.getUUID();
        this.amount = amount;
        if (amount > 0) {
            this.type = Movement_Type.payment;
        } else {
            this.type = Movement_Type.withdrawal;
        }
    }

    public Movement(String id, float amount, String type, Date dataTransazione) {
        this.id = id;
        this.amount = amount;
        this.type = Movement_Type.valueOf(type);
        this.dataTransazione = dataTransazione;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Movement_Type getType() {
        return type;
    }

    public void setType(Movement_Type type) {
        this.type = type;
    }

    public Date getDataTransazione() {
        return dataTransazione;
    }

    public void setDataTransazione(Date dataTransazione) {
        this.dataTransazione = dataTransazione;
    }
}
