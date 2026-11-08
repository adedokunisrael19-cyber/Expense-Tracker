package ng.expensetracker.dto.response;

import ng.expensetracker.data.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,

        TransactionType type,

        BigDecimal amount,

        String description,

        LocalDate transactionDate,

        Long categoryId,

        String categoryName,

        Instant createdAt
) {

}
