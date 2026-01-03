package services;

import model.StandingOrder;
import model.Iban;
import java.time.LocalDateTime;

public class StandingOrderFactory {

    public static StandingOrder createTransferOrder(String sourceIban, String targetIban, double amount, String desc, int day, LocalDateTime expireDate) {
        return new StandingOrder(
            new Iban(sourceIban),
            targetIban,
            amount,
            desc,
            day,
            StandingOrder.StandingOrderPurpose.TRANSFER,
            expireDate
        );
    }

    public static StandingOrder createBillPaymentOrder(String sourceIban, String rfCode, double amount, String desc, int day, LocalDateTime expireDate) {
        return new StandingOrder(
            new Iban(sourceIban),
            rfCode,
            amount,
            desc,
            day,
            StandingOrder.StandingOrderPurpose.BILL,
            expireDate
        );
    }
}