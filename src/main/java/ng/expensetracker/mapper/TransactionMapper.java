package ng.expensetracker.mapper;

import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.dto.response.TransactionResponse;

public class TransactionMapper {

    private TransactionMapper() {
        // Prevent creating instances of this utility class
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getCreatedAt()
        );
    }
}