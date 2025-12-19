package model;

import java.time.LocalDateTime;
import java.util.UUID; // Χρειάζεται για τυχαίο ID

public class StandingOrder {
    
    private String standinID;
    private double amount;
    private String description;
    private Iban source;
    private Iban target;
    private String targetRfCode;
    private int freq;
    private LocalDateTime nexTime;
    private LocalDateTime expiredDay;
    private StandingOrderPurpose type;
    private OrderStatus status;
    private int attempts;

    private final int NEXT_TIME_ON_FAILED = 2; // two days
    
   public enum StandingOrderPurpose{
        TRANSFER,
        BILL
    }
    
    public enum OrderStatus {
        ACTIVE,       
        COMPLETED,    
        FAILD       
    }    

    public StandingOrder() {}

    // --- 1. O ΥΠΑΡΧΩΝ ΜΕΓΑΛΟΣ CONSTRUCTOR (Τον κρατάμε για συμβατότητα) ---
    public StandingOrder(String standinID, double amount, String description, Iban source, Iban target,
            String targetRfCode, int freq, LocalDateTime expiredDay, String type) {
        this.standinID = standinID;
        this.amount = amount;
        this.description = description;
        this.source = source;
        this.target = target;
        this.targetRfCode = targetRfCode;
        this.freq = freq;
        this.attempts = 0;
        this.nexTime = LocalDateTime.now().plusDays(freq);
        this.expiredDay = expiredDay;
        
        if(type.equals("TRANSFER")){
            this.type = StandingOrderPurpose.TRANSFER;
        } else if (type.equals("BILL")){
            this.type = StandingOrderPurpose.BILL;
        }

        this.status = OrderStatus.ACTIVE;
    }

    // --- 2. Ο ΝΕΟΣ CONSTRUCTOR ΠΟΥ ΛΕΙΠΕΙ (Για το TransactionPage) ---
    public StandingOrder(Iban source, Iban target, double amount, String description, StandingOrderPurpose type) {
        // Δημιουργία τυχαίου ID
        this.standinID = UUID.randomUUID().toString().substring(1, 8);
        
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.description = description;
        this.type = type;
        
        // Default τιμές επειδή η απλή φόρμα δεν τις ζητάει
        this.freq = 30; // Default: Κάθε μήνα
        this.targetRfCode = "-";
        this.status = OrderStatus.ACTIVE;
        this.attempts = 0;
        this.nexTime = LocalDateTime.now().plusDays(freq); // Πρώτη εκτέλεση σε 30 μέρες
        this.expiredDay = LocalDateTime.now().plusYears(1); // Λήξη σε 1 χρόνο
    }

    // --- LOGIC ---

    public void updateNextTime(){
        this.nexTime = this.nexTime.plusDays(freq);
        this.attempts = 0;
        if(nexTime.isAfter(expiredDay))
            this.status = OrderStatus.COMPLETED;
    }

    public void failStanding(){
        this.attempts++; // 0 1 2 (3 attempts)
        if(this.attempts >= 3){
            this.status = OrderStatus.FAILD;
        } else {
            this.nexTime = this.nexTime.plusDays(NEXT_TIME_ON_FAILED);
        }
    }
        
    @Override
    public String toString() {
        return "Order{" + getStandinID() + ", type=" + getType().toString() + ", next=" + getNexTime() + ", status=" + getStatus() + "}";
    }
 
    // --- GETTERS & SETTERS ---

    public String getStandinID() { return standinID; }
    public void setStandinID(String standinID) { this.standinID = standinID; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Iban getSource() { return source; }
    public void setSource(Iban source) { this.source = source; }

    public Iban getTarget() { return target; }
    public void setTarget(Iban target) { this.target = target; }

    public String getTargetRfCode() { return targetRfCode; }
    public void setTargetRfCode(String targetRfCode) { this.targetRfCode = targetRfCode; }

    public int getFreq() { return freq; }
    public void setFreq(int freq) { this.freq = freq; }

    public LocalDateTime getNexTime() { return nexTime; }
    public void setNexTime(LocalDateTime nexTime) { this.nexTime = nexTime; }

    public LocalDateTime getExpiredDay() { return expiredDay; }
    public void setExpiredDay(LocalDateTime expiredDay) { this.expiredDay = expiredDay; }

    public StandingOrderPurpose getType() { return type; }
    public void setType(StandingOrderPurpose type) { this.type = type; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}