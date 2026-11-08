package ng.expensetracker.data.repository;

import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.data.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void testShouldRetrieveOnlyActiveTransactions() {
        Category category = categoryRepository.save(new Category(
                "Groceries",
                TransactionType.EXPENSE
        ));

        Transaction activeTransaction = transactionRepository.save(
                new Transaction(TransactionType.EXPENSE, new BigDecimal("5000.00"), "Active transaction", LocalDate.now(), category)
        );

        Transaction deletedTransaction = new Transaction(TransactionType.EXPENSE, new BigDecimal("2000.00"), "Deleted", LocalDate.now(), category);

        deletedTransaction.softDelete();
        transactionRepository.save(deletedTransaction);

        List<Transaction> result = transactionRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(activeTransaction.getId());
    }

    @Test
    void shouldRetrievePaginatedActiveTransactions() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        categoryRepository.save(category);

        Transaction transaction1 = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.now(),
                category
        );

        Transaction transaction2 = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("3000.00"),
                "Food supplies",
                LocalDate.now(),
                category
        );

        Transaction deletedTransaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("2000.00"),
                "Deleted transaction",
                LocalDate.now(),
                category
        );

        deletedTransaction.softDelete();

        transactionRepository.saveAll(
                List.of(transaction1, transaction2, deletedTransaction)
        );

        Pageable pageable = PageRequest.of(0, 1);

        Page<Transaction> result =
                transactionRepository.findAllByDeletedFalse(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
    }

    @Test
    void shouldCheckIfCategoryIsReferencedByTransaction() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        categoryRepository.save(category);

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.now(),
                category
        );

        transactionRepository.save(transaction);

        boolean result = transactionRepository.existsByCategoryId(category.getId());

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCategoryIsNotReferenced() {

        Category category = new Category(
                "Transportation",
                TransactionType.EXPENSE
        );

        categoryRepository.save(category);

        boolean result =
                transactionRepository.existsByCategoryId(category.getId());

        assertThat(result).isFalse();
    }
}