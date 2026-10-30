package ng.expensetracker.services;

import ng.expensetracker.data.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionService {
    void createTransaction(TransactionType transactionType, BigDecimal bigDecimal, String groceries, LocalDate now, long l);
}
