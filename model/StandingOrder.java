package model;

import java.time.LocalDateTime;

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

    private final int next_time_on_faild = 2; //two days
    
    
    enum StandingOrderPurpose{
        TRANSFER,
        BILL
    }
    
    enum OrderStatus {
        ACTIVE,       
        COMPLETED,    
        FAILD       
    }    
    
   

    public StandingOrder() {}

    public StandingOrder(String standinID, double amount, String description, Iban source, Iban target,
            String targetRfCode, int freq, LocalDateTime expiredDay, String type) {
        this.standinID = standinID;
        this.amount = amount;
        this.description = description;
        this.source = source;
        this.target = target;
        this.targetRfCode = targetRfCode;
        this.freq = freq;
        this.attempts=0;
        this.nexTime = LocalDateTime.now().plusDays(freq);
        this.expiredDay = expiredDay;
        if(type.equals("TRANSFER")){
            this.type = StandingOrderPurpose.TRANSFER;
        }
        else if (type.equals("BILL")){
            this.type = StandingOrderPurpose.BILL;
        }

        this.status = OrderStatus.ACTIVE;
    }

    public void updateNextTime(){
        this.nexTime = this.nexTime.plusDays(freq);
        this.attempts = 0;
        if(nexTime.isAfter(expiredDay))
            this.status = OrderStatus.COMPLETED;
    }
    public void failStanding(){
        this.attempts++;//0 1 2 (3 attempts)
        if(this.attempts>=3){
            this.status=OrderStatus.FAILD;
        }else{
            this.nexTime = this.nexTime.plusDays(next_time_on_faild);
        }
    }
        

@Override
    public String toString() {
        return "Order{" + getStandinID() + ", type=" + getType().toString() + ", next=" + getNexTime() + ", status=" + getStatus() + "}";
    }
 
    public String getStandinID() {
        return standinID;
    }

    public void setStandinID(String standinID) {
        this.standinID = standinID;
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

    public String getTargetRfCode() {
        return targetRfCode;
    }

    public void setTargetRfCode(String targetRfCode) {
        this.targetRfCode = targetRfCode;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public LocalDateTime getNexTime() {
        return nexTime;
    }

    public void setNexTime(LocalDateTime nexTime) {
        this.nexTime = nexTime;
    }

    public LocalDateTime getExpiredDay() {
        return expiredDay;
    }

    public void setExpiredDay(LocalDateTime expiredDay) {
        this.expiredDay = expiredDay;
    }

    public StandingOrderPurpose getType() {
        return type;
    }

    public void setType(StandingOrderPurpose type) {
        this.type = type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
