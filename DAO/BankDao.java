package DAO;

 

public interface BankDao {
    void save(BankData data);
    BankData load();
}