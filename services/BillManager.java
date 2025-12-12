package services;

import model.Bill;
import model.Bill.Status; // Σιγουρέψου ότι το Status είναι public στο Bill
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

    public List<Bill> getAllBills() {
        return bills;
    }
 
    public void payBill(String rfCode, String payerIban, String payerAfm, TransactionManager tm) throws Exception {
        
         Bill bill = getBillByRf(rfCode);
        
        if (bill == null) {
            throw new Exception("Bill RF " + rfCode + " not found.");
        }

         if (bill.getBillStatus() == Status.PAID) {
            throw new Exception("bill already paid");
        }
 
        tm.withdraw(payerIban, bill.getAmount(), "Payment of Bill RF: " + rfCode);

         bill.pay(payerAfm);
        
        System.out.println("Bill " + rfCode + " paid successfully by " + payerAfm);
    }

    public void markAsPaid(String rfCode, String payerAfm) {
        Bill b = getBillByRf(rfCode);
        if (b != null) {
            b.pay(payerAfm);  
            System.out.println("Bill " + rfCode + " marked as PAID.");
        }
    }

    
}