package ng.expensetracker.data.repository;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.data.model.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldRejectNegativeTransactionAmount() {
        Transaction transaction = new Transaction(
                TransactionType.EXPENSE, new BigDecimal("-500"),
                "Invalid expense", LocalDate.now(),
                null
        );

        var violations = validator.validate(transaction);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("amount");
    }


    @Test
    void shouldRejectAmountWithMoreThanTwoDecimalPlaces() {

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("1500.567"),
                "Invalid precision",
                LocalDate.now(),
                null
        );

        var violations = validator.validate(transaction);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("amount");
    }

    @Test
    void shouldRejectZeroAmount() {

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                BigDecimal.ZERO,
                "Zero amount",
                LocalDate.now(),
                null
        );

        var violations = validator.validate(transaction);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("amount");
    }


    @Test
    void shouldRejectNullTransactionType() {

        Transaction transaction = new Transaction(
                null,
                new BigDecimal("5000.00"),
                "Groceries",
                LocalDate.now(),
                null
        );

        var violations = validator.validate(transaction);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("type");
    }


    @Test
    void shouldRejectDescriptionLongerThan500Characters() {

        String longDescription = "a".repeat(501);

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                longDescription,
                LocalDate.now(),
                null
        );

        var violations = validator.validate(transaction);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("description");
    }


    @Test
    void shouldRejectTransactionWithoutCategory() {

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Groceries",
                LocalDate.now(),
                null
        );

        var violations = validator.validate(transaction);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("category");
    }


}
