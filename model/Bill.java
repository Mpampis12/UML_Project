package model;

import java.time.LocalDate;

public class Bill {

    private String rfCode;
    private double amount;
    private String description;
    private LocalDate dateOfIssue;
    private LocalDate expireDate;
    private String targetIban;
    private String buisinessAfm;
    private String payerAfm;
    private Status billStatus;




    public String getRfCode() {
        return rfCode;
    }

    public void setRfCode(String rfCode) {
        this.rfCode = rfCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(LocalDate dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getBuisinessAfm() {
        return buisinessAfm;
    }

    public void setBuisinessAfm(String buisinessAfm) {
        this.buisinessAfm = buisinessAfm;
    }

    public String getPayerAfm() {
        return payerAfm;
    }

    public void setPayerAfm(String payerAfm) {
        this.payerAfm = payerAfm;
    }

    public Status getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Status billStatus) {
        this.billStatus = billStatus;
    }

    public enum Status {
        PENDING,  
        PAID,     
        EXPIRED  
    }

    public Bill(){}

    public Bill(String rfCode, String targetIban, double amount, String description, String buisinessAfm, LocalDate expireDate) {
        this.rfCode = rfCode;
        this.amount = amount;
        this.description = description;
        this.buisinessAfm = buisinessAfm;
        this.dateOfIssue = LocalDate.now();
        this.expireDate = expireDate;
        this.billStatus = Status.PENDING;  
        this.payerAfm = null;
        this.targetIban = targetIban;
    }
     public void pay(String payerAfm) {
        this.billStatus = Status.PAID;
        this.payerAfm = payerAfm;
    }
    public String getTargetIban() { return targetIban; }  
    public void setTargetIban(String targetIban) { this.targetIban = targetIban; }
}
