package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class StandingOrder {
    
    private String standinID;
    private double amount;
    private String description; // Details
    private Iban source;
    private Iban target; // Μπορεί να είναι null αν είναι RF
    private String targetRfCode; // Μπορεί να είναι "-" αν είναι IBAN
    private int dayOfMonth; // 1-30
    private LocalDateTime nexTime;
    private LocalDateTime expiredDay;
    private StandingOrderPurpose type;
    private OrderStatus status;
    public void setStandinID(String standinID) {
        this.standinID = standinID;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSource(Iban source) {
        this.source = source;
    }

    public void setTarget(Iban target) {
        this.target = target;
    }

    public void setTargetRfCode(String targetRfCode) {
        this.targetRfCode = targetRfCode;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setNexTime(LocalDateTime nexTime) {
        this.nexTime = nexTime;
    }

    public void setExpiredDay(LocalDateTime expiredDay) {
        this.expiredDay = expiredDay;
    }

    public void setType(StandingOrderPurpose type) {
        this.type = type;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    private int attempts;

    private final int NEXT_TIME_ON_FAILED = 2; 

    public enum StandingOrderPurpose{ TRANSFER, BILL }
    public enum OrderStatus { ACTIVE, COMPLETED, FAILD }    

    public StandingOrder() {}

    // ΝΕΟΣ ΠΛΗΡΗΣ CONSTRUCTOR
    public StandingOrder(Iban source, String targetIdentifier, double amount, String description, int dayOfMonth, StandingOrderPurpose type) {
        this.standinID = UUID.randomUUID().toString().substring(0, 8);
        this.source = source;
        this.amount = amount;
        this.description = description;
        this.dayOfMonth = dayOfMonth;
        this.type = type;
        this.status = OrderStatus.ACTIVE;
        this.attempts = 0;
        this.expiredDay = LocalDateTime.now().plusYears(1);

        if (type == StandingOrderPurpose.TRANSFER) {
            this.target = new Iban(targetIdentifier);
            this.targetRfCode = "-";
        } else {
            this.target = null; // ή κάποιο dummy
            this.targetRfCode = targetIdentifier;
        }
        
        calculateNextTime();
    }

    private void calculateNextTime() {
        LocalDateTime now = LocalDateTime.now();
        // Αν η σημερινή μέρα είναι πριν τη μέρα εκτέλεσης, εκτέλεσε τον τρέχοντα μήνα
        // Αλλιώς τον επόμενο.
        if (now.getDayOfMonth() < dayOfMonth) {
             this.nexTime = now.withDayOfMonth(Math.min(dayOfMonth, now.toLocalDate().lengthOfMonth()));
        } else {
             this.nexTime = now.plusMonths(1).withDayOfMonth(Math.min(dayOfMonth, now.plusMonths(1).toLocalDate().lengthOfMonth()));
        }
    }

    public void updateDetails(String targetIdentifier, double amount, String desc, int day) {
        this.amount = amount;
        this.description = desc;
        this.dayOfMonth = day;
        
        if (this.type == StandingOrderPurpose.TRANSFER) {
            this.target = new Iban(targetIdentifier);
        } else {
            this.targetRfCode = targetIdentifier;
        }
        calculateNextTime(); // Recalculate execution
    }

    // Getters
    public String getStandinID() { return standinID; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public Iban getSource() { return source; }
    public Iban getTarget() { return target; }
    public String getTargetRfCode() { return targetRfCode; }
    public int getDayOfMonth() { return dayOfMonth; }
    public LocalDateTime getNexTime() { return nexTime; }
    public StandingOrderPurpose getType() { return type; }
    public OrderStatus getStatus() { return status; }
    
    // Setters (για GSON & Logic)
    public void updateNextTime(){
        this.nexTime = this.nexTime.plusMonths(1); // Προσθέτει ένα μήνα
        // Διόρθωση ημέρας (π.χ. αν είναι 30 και ο μήνας έχει 28)
        int maxDay = this.nexTime.toLocalDate().lengthOfMonth();
        if (this.dayOfMonth > maxDay) {
            this.nexTime = this.nexTime.withDayOfMonth(maxDay);
        } else {
            this.nexTime = this.nexTime.withDayOfMonth(this.dayOfMonth);
        }
        this.attempts = 0;
        if(nexTime.isAfter(expiredDay)) this.status = OrderStatus.COMPLETED;
    }
    
    public void failStanding(){
        this.attempts++;
        if(this.attempts >= 3) this.status = OrderStatus.FAILD;
        else this.nexTime = this.nexTime.plusDays(NEXT_TIME_ON_FAILED);
    }
}