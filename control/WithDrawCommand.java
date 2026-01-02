package control;

import services.BankSystem;
import services.TransactionManager;

public class WithDrawCommand implements BankCommandPattern {

    private TransactionManager transactionManager;
    private String iban;
    private double amount;
    private String description;

    public WithDrawCommand(TransactionManager manager, String iban, double amount, String description) {
        this.transactionManager = manager;
        this.iban = iban;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public void execute() throws Exception {
        transactionManager.withdraw(iban, amount, description,BankSystem.getInstance().getTimeSimulator().getCurrentDate() );
    }
}