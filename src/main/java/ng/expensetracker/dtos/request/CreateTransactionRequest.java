package ng.expensetracker.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ng.expensetracker.data.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTransactionRequest {

    @NotNull(message = "Transaction type is required")
    TransactionType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 12, fraction = 2,
            message = "Amount must have at most 12 integer digits and 2 decimal places")
    BigDecimal amount;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description;

    @NotNull(message = "Transaction date is required")
    LocalDate transactionDate;

    @NotNull(message = "Category ID is required")
    Long categoryId;

}
