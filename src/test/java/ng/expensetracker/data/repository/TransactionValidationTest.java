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

        assertThat(violations).isNotEmpty();
    }
}
