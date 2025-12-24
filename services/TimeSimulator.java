package services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TimeSimulator implements Runnable {

    private LocalDate currentDate;
    private boolean running;
    private int simulationSpeedMs = 5000; //5 sec is one mera
    
 
    private Consumer<LocalDate> dateChangeListener;

    public TimeSimulator() {
         this.currentDate = LocalDate.now(); 
        this.running = true;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }
    
     public void setDateChangeListener(Consumer<LocalDate> listener) {
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