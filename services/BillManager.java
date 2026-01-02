package services;

import model.Bill;
import model.Bill.Status;
import java.util.ArrayList;
import java.util.List;

public class BillManager {

    private List<Bill> bills;

    public BillManager() {
        this.bills = new ArrayList<>();
    }

    public void addBill(Bill bill) {
        this.bills.add(bill);
        System.out.println("New Bill added: " + bill.getRfCode());
    }

    public Bill getBillByRf(String rfCode) {
        for (Bill b : bills) {
            if (b.getRfCode().equals(rfCode)) {
                return b;
            }
        }
        return null;
    }

    public List<Bill> getAllBills() { return bills; }
    public void setBills(List<Bill> bills) { this.bills = bills; }
 
    public void payBill(String rfCode, String payerIban, String payerAfm, TransactionManager tm) throws Exception {
        
        Bill bill = getBillByRf(rfCode);
        
        if (bill == null) {
            throw new Exception("Bill RF " + rfCode + " not found.");
        }

        if (bill.getBillStatus() == Status.PAID) {
            throw new Exception("Bill already paid");
        }
         
         
        tm.withdraw(payerIban, bill.getAmount(), "Payment of Bill RF: " + rfCode,BankSystem.getInstance().getTimeSimulator().getCurrentDate());
 
        tm.deposit(bill.getTargetIban(), bill.getAmount(), "Bill Payment Received RF: " + rfCode + " from " + payerAfm,BankSystem.getInstance().getTimeSimulator().getCurrentDate() );

      
        bill.pay(payerAfm);
        markAsPaid(rfCode,payerAfm);
        
        System.out.println("Bill " + rfCode + " paid successfully by " + payerAfm);
    }
    public void markAsPaid(String targetRfCode, String payerAfm) {
        // 1. Βρες τον λογαριασμό στη λίστα
        Bill bill = getBillByRf(targetRfCode);
        
        // 2. Αν υπάρχει, άλλαξε την κατάστασή του
        if (bill != null) {
            bill.pay(payerAfm); // Θέτει status = PAID και αποθηκεύει το payerAfm
            System.out.println("Bill " + targetRfCode + " marked as PAID manually.");
        } else {
            System.err.println("Bill with RF " + targetRfCode + " not found.");
        }
    }
    
}