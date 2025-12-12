package services;

import java.time.LocalDate;

public class TimeSimulator {

    private LocalDate currentSystemDate;

    public TimeSimulator() {
        this.currentSystemDate = LocalDate.now(); // Ξεκινάμε από σήμερα
    }

    public LocalDate getCurrentDate() {
        return currentSystemDate;
    }

    /**
     * Προχωράει τον χρόνο κατά 'days' ημέρες.
     * Για κάθε μέρα που περνάει, ειδοποιεί το BankSystem να κάνει τις δουλειές του.
     */
    public void advanceTime(int days) {
        System.out.println("\n*** STARTING SIMULATION FOR " + days + " DAYS ***");
        
        for (int i = 0; i < days; i++) {
            // Προχωράμε μία μέρα
            currentSystemDate = currentSystemDate.plusDays(1);
            System.out.println("\n[Date: " + currentSystemDate + "]");
            
            // Καλούμε το BankSystem να τρέξει τις καθημερινές εργασίες
            BankSystem.getInstance().performDailyTasks(currentSystemDate);
        }
        
        System.out.println("*** SIMULATION COMPLETED ***\n");
    }
}