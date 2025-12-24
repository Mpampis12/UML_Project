package view;

import model.Account;
import model.User;

public interface BankBridge {
    void showLogin();
    void showDashboard(User user);
    void showHistory(User user);
    void showTransactions(User user);
    void showRegister();
    void showAccountDetails(User user, Account account);
    void showCreateAccountConfirmation(User user);
  }