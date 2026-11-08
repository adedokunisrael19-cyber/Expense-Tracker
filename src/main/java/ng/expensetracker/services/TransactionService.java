package ng.expensetracker.services;

import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.data.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Transaction createTransaction(TransactionType transactionType, BigDecimal bigDecimal, String groceries, LocalDate now, long l);

    Transaction getTransaction(long l);

    Transaction updateTransaction(long transactionId, TransactionType transactionType, BigDecimal amount, String description, LocalDate transactionDate, long categoryId);

    void deleteTransaction(long id);

    List<Transaction> getAllTransactions();

    Page<Transaction> getAllTransactions(Pageable pageable);
}
