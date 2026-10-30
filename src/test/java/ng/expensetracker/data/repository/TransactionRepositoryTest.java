package ng.expensetracker.data.repository;

import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.data.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)

class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testThatICanSaveAndRetrieve(){
        Category category = new Category("Groceries", TransactionType.EXPENSE);
        Category savedCategory = categoryRepository.save(category);

        Transaction transaction = new Transaction(TransactionType.EXPENSE,
                new BigDecimal("5000.00"), "Weekly groceries",
                LocalDate.now(), savedCategory);

        Transaction savedTransaction = transactionRepository.save(transaction);


        assertThat(savedTransaction.getId()).isNotNull();

        assertThat(savedTransaction.getAmount())
                .isEqualByComparingTo("5000.00");

        assertThat(savedTransaction.getType())
                .isEqualTo(TransactionType.EXPENSE);

        assertThat(savedTransaction.getCategory().getName())
                .isEqualTo("Groceries");

    }

}