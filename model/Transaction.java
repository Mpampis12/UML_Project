package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String transactionID;
    private double amount;
    private TransactionType type;
    private String description;
    private Iban source;
    private Iban target;
    private LocalDateTime timestamp;

    public enum TransactionType{
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        PAYMENT
    }

///Build adapter gia na apofyw polloys constructors
    private Transaction(Builder builder){
        this.transactionID = builder.transactionId;
        this.timestamp = builder.timestamp;
        this.amount = builder.amount;
        this.type = TransactionType.valueOf(builder.type);
        this.description = builder.description;
        this.source = builder.sourceIban == null ? null : new Iban(builder.sourceIban);
        this.target = builder.targetIban == null ? null : new Iban(builder.targetIban);
    }

    
@Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionID + '\'' +
                ", date=" + timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }


    public String getTransactionID() {
        return transactionID;
    }



    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }



    public double getAmount() {
        return amount;
    }



    public void setAmount(double amount) {
        this.amount = amount;
    }



    public TransactionType getType() {
        return type;
    }



    public void setType(TransactionType type) {
        this.type = type;
    }



    public String getDescription() {
        return description;
    }



    public void setDescription(String description) {
        this.description = description;
    }



    public Iban getSource() {
        return source;
    }



    public void setSource(Iban source) {
        this.source = source;
    }



    public Iban getTarget() {
        return target;
    }



    public void setTarget(Iban target) {
        this.target = target;
    }

    // Static nested Builder class
   public static class Builder {
        private String transactionId;
        private double amount;
        private String type;

        // Default τιμές
        private LocalDateTime timestamp = LocalDateTime.now();
        private String description = "";
        private String sourceIban = null;
        private String targetIban = null;

        public Builder(String transactionId, String type, double amount) {
            this.transactionId = transactionId;
            this.type = type;
            this.amount = amount;
        }
           public Builder() {
 
        }

        public Builder setTimestamp(LocalDateTime t) { this.timestamp = t; return this; }
        public Builder setDescription(String d) { this.description = d; return this; }
        public Builder setSourceIban(String s) { this.sourceIban = s; return this; }
        public Builder setTargetIban(String t) { this.targetIban = t; return this; }
        public Builder setAmount(double d ) { this.amount = d; return this; }

        public Transaction build() { return new Transaction(this); }
    }
}
