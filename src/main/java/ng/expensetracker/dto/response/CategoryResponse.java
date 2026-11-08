package ng.expensetracker.dto.response;


import ng.expensetracker.data.model.TransactionType;

import java.time.Instant;

public record CategoryResponse(
        Long id,
        String name,
        TransactionType type,
        Instant createdAt
) {
}