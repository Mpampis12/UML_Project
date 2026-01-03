package services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TimeSimulator implements Runnable {

    private LocalDateTime currentDate;
    private boolean running;
    private int simulationSpeedMs = 5000000; //1 sec is one mera
    
 
    private Consumer<LocalDateTime> dateChangeListener;

    public TimeSimulator() {
         this.currentDate = LocalDateTime.now().minusDays(2); 
        this.running = true;
    }

    public LocalDateTime getCurrentDate() {
        return currentDate;
    }
    
     public void setDateChangeListener(Consumer<LocalDateTime> listener) {
        this.dateChangeListener = listener;
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                 Thread.sleep(simulationSpeedMs);

                 currentDate = currentDate.plusDays(1);
                
                 if (dateChangeListener != null) {
                    dateChangeListener.accept(currentDate);
                }

                System.out.println("New Day: " + currentDate);

            } catch (InterruptedException e) {
                System.out.println("Time Simulator Interrupted");
                running = false;
            }
        }
    }
}