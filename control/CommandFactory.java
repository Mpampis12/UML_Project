package control;

import services.TransactionManager;

public class CommandFactory {
    
    public static BankCommandPattern createCommand(String type, TransactionManager tm, String source, String target, double amount, String desc) {
        switch (type) {
            case "DEPOSIT":
                return new DepositCommand(tm, source, amount, desc);
            case "WITHDRAW":
                return new WithDrawCommand(tm, source, amount, desc);
            case "TRANSFER":
                return new TransferCommand(tm, source, target, amount, desc);
            default:
                throw new IllegalArgumentException("Unknown command type: " + type);
        }
    }
}