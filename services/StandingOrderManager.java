package services;

import model.StandingOrder;
import model.StandingOrder.OrderStatus;
import model.StandingOrder.StandingOrderPurpose;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StandingOrderManager {

    private List<StandingOrder> orders;

    public StandingOrderManager() {
        this.orders = new ArrayList<>();
    }

    public void addOrder(StandingOrder order) {
        this.orders.add(order);
        System.out.println("Standing Order added: " + order.getStandinID());
    }

    public List<StandingOrder> getOrders() {
        return orders;
    }
 
    public void executeDailyOrders(LocalDate currentDate, TransactionManager tm, BillManager bm) {
        System.out.println("--- Checking Standing Orders for date: " + currentDate + " ---");

        for (StandingOrder order : orders) {
          
            if (order.getStatus() == OrderStatus.ACTIVE && 
                !order.getNexTime().toLocalDate().isAfter(currentDate)) {
                
                try {
                    System.out.println("Executing Order: " + order.getStandinID());

                    if (order.getType() == StandingOrderPurpose.TRANSFER) {
              
                        tm.transfer(
                            order.getSource().toString(), 
                            order.getTarget().toString(), 
                            order.getAmount(), 
                            "Standing Order: " + order.getDescription()
                        );
                        
                    } else if (order.getType() == StandingOrderPurpose.BILL) {
                  
                        tm.withdraw(
                            order.getSource().toString(), 
                            order.getAmount(), 
                            "Bill Payment RF: " + order.getTargetRfCode()
                        );
                        
                         bm.markAsPaid(order.getTargetRfCode(), "AUTO-PAYMENT");
                    }

                     order.updateNextTime();
                    System.out.println("Order " + order.getStandinID() + " success. Next execution: " + order.getNexTime());

                } catch (Exception e) {
                     System.out.println("Order " + order.getStandinID() + " FAILED: " + e.getMessage());
                    order.failStanding();
                }
            }
        }
    }
    public void setOrders(List<StandingOrder> orders) {
            this.orders = orders;
    }
    public void deleteOrder(StandingOrder order) {
        orders.remove(order);
     }
}