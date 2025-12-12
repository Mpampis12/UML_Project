package view;

import model.User;

public interface BankBridge {
    void showLogin();
    void showDashboard(User user);
    void showHistory(User user);
    void showTransactions(User user);
}