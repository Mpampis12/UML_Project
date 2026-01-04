package services;

import model.Bill;
import model.Bill.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import control.BankController;

public class BillManager {

    private List<Bill> bills;

    public BillManager() {
        this.bills = new ArrayList<>();
    }

    public void addBill(Bill bill) {
        this.bills.add(bill);
        System.out.println("New Bill added: " + bill.getRfCode());
    }

    // --- ΔΙΟΡΘΩΜΕΝΗ ΜΕΘΟΔΟΣ ---
    public Bill getBillByRf(String rfCode) {
        // 1. Πρώτα ψάχνουμε για ΑΠΛΗΡΩΤΟ (PENDING) λογαριασμό
        // Αυτό λύνει το πρόβλημα με τα Standing Orders που έβρισκαν τον παλιό (PAID)
        for (Bill b : bills) {
            if (b.getRfCode().equals(rfCode) && b.getBillStatus() == Status.PENDING) {
                return b;
            }
        }
        
        // 2. Αν δεν υπάρχει Pending, επιστρέφουμε τον παλιό (π.χ. PAID/EXPIRED)
        // για να έχουμε το ιστορικό ή για να λειτουργεί το Renew.
        for (Bill b : bills) {
            if (b.getRfCode().equals(rfCode)) {
                return b;
            }
        }
        return null;
    }
    // -------------------------

    public List<Bill> getAllBills() { return bills; }
    public void setBills(List<Bill> bills) { this.bills = bills; }
 
    public void payBill(String rfCode, String payerIban, String payerAfm, TransactionManager tm) throws Exception {
        
        // Τώρα αυτό θα φέρει τον σωστό (PENDING) λογαριασμό, αν υπάρχει
        Bill bill = getBillByRf(rfCode);
        
        if (bill == null) {
            throw new Exception("Bill RF " + rfCode + " not found.");
        }

        if (bill.getBillStatus() == Status.PAID) {
            throw new Exception("Bill already paid");
        }
        if (bill.getBillStatus() == Status.EXPIRED) {
            throw new Exception("Bill Expired on " + bill.getExpireDate());
        }
         
        // Εκτέλεση συναλλαγών
        tm.withdraw(payerIban, bill.getAmount(), "Payment of Bill RF: " + rfCode, BankSystem.getInstance().getTimeSimulator().getCurrentDate());
        tm.deposit(bill.getTargetIban(), bill.getAmount(), "Bill Payment Received RF: " + rfCode + " from " + payerAfm, BankSystem.getInstance().getTimeSimulator().getCurrentDate());

        // Ενημέρωση κατάστασης
        bill.pay(payerAfm); 
        
        System.out.println("Bill " + rfCode + " paid successfully by " + payerAfm);
    }

    public void checkExpiredBills(LocalDateTime currentDate) {
        for (Bill bill : bills) {
            if (bill.getBillStatus() == Status.PENDING && currentDate.isAfter(bill.getExpireDate())) {
                bill.setBillStatus(Status.EXPIRED);
                 
                System.out.println("Bill " + bill.getRfCode() + " has EXPIRED.");
            }
        }
    }
    
    public void markAsPaid(String targetRfCode, String payerAfm) {
        Bill bill = getBillByRf(targetRfCode);
        if (bill != null) {
            bill.pay(payerAfm); 
            System.out.println("Bill " + targetRfCode + " marked as PAID manually.");
        } else {
            System.err.println("Bill with RF " + targetRfCode + " not found.");
        }
    }
}