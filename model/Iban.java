package model;

import java.util.Random;

public class Iban {
    
    private final String ibanNumber;

    public Iban(String ibanNumber){
        if (!isValid(ibanNumber)) {
            throw new IllegalArgumentException("Μη έγκυρο IBAN: " + ibanNumber);
        }
        else{
        this.ibanNumber = ibanNumber;
        }
    }

    public static boolean isValid(String iban) {
        // Simple validation: check length and prefix
        return iban != null && iban.startsWith("GR") && iban.length() == 20 && iban.matches("GR\\d{18}");
    }

    public static Iban generate() {
        StringBuilder sb = new StringBuilder("GR");
        Random random = new Random();
        
        for (int i = 0; i < 18; i++) {
            sb.append(random.nextInt(10));  
        }
        
        return new Iban(sb.toString()); 
    }
    @Override
    public String toString() {
        return ibanNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Iban iban = (Iban) o;
        return ibanNumber.equals(iban.ibanNumber);
    }
    
    @Override
    public int hashCode() {
        return ibanNumber.hashCode();
    }
}
