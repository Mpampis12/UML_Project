package DAO;

import model.*;
import java.util.List;
import java.util.ArrayList;

 public class BankData {
    public List<Admin> admins = new ArrayList<>();
    public List<Customer> customers = new ArrayList<>();
    public List<Account> accounts = new ArrayList<>();
    public List<Bill> bills = new ArrayList<>();
    public List<StandingOrder> standingOrders = new ArrayList<>();
}