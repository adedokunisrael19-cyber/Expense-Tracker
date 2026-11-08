package ng.expensetracker.dto.request;

import ng.expensetracker.data.model.TransactionType;

import java.time.Instant;

public record CategoryRequest() {
    static Long id;
    static String name;
    static TransactionType type;
    static Instant createdAt;
}
