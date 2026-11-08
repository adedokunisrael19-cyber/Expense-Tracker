package ng.expensetracker.services;


import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.data.model.TransactionType;
import ng.expensetracker.data.repository.CategoryRepository;
import ng.expensetracker.data.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;

    @Test
    void shouldRejectExpenseTransactionWhenCategoryIsIncome() {

        Category incomeCategory = new Category(
                "Salary",
                TransactionType.INCOME
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(incomeCategory));
        assertThatThrownBy(() ->
                transactionServiceImpl.createTransaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("5000.00"),
                        "Groceries",
                        LocalDate.now(),
                        1L
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction type must match category type");

        verify(transactionRepository, never()).save(any());
    }


    @Test
    void shouldRejectTransactionWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionServiceImpl.createTransaction(
                        TransactionType.EXPENSE,
                        new BigDecimal("5000.00"),
                        "Groceries",
                        LocalDate.now(),
                        99L
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category not found");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldCreateTransactionSuccessfully() {

        Category expenseCategory = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(expenseCategory));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionServiceImpl.createTransaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.now(),
                1L
        );

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(result.getAmount()).isEqualByComparingTo("5000.00");
        assertThat(result.getDescription()).isEqualTo("Weekly groceries");
        assertThat(result.getCategory()).isEqualTo(expenseCategory);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldRetrieveTransactionById() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.now(),
                category
        );

        when(transactionRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(transaction));

        Transaction result = transactionServiceImpl.getTransaction(1L);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualByComparingTo("5000.00");
        assertThat(result.getDescription()).isEqualTo("Weekly groceries");
        assertThat(result.getCategory()).isEqualTo(category);

        verify(transactionRepository).findByIdAndDeletedFalse(1L);
    }

    @Test
    void shouldRejectRetrievalWhenTransactionDoesNotExist() {

        when(transactionRepository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionServiceImpl.getTransaction(99L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction not found");
    }

    @Test
    void shouldUpdateTransactionSuccessfully() {

        Category oldCategory = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        Category newCategory = new Category(
                "Salary",
                TransactionType.INCOME
        );

        Transaction existingTransaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.now(),
                oldCategory
        );

        when(transactionRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(existingTransaction));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(newCategory));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionServiceImpl.updateTransaction(
                1L,
                TransactionType.INCOME,
                new BigDecimal("150000.00"),
                "Monthly salary",
                LocalDate.now(),
                2L
        );

        assertThat(result.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(result.getAmount()).isEqualByComparingTo("150000.00");
        assertThat(result.getDescription()).isEqualTo("Monthly salary");
        assertThat(result.getCategory()).isEqualTo(newCategory);

        verify(transactionRepository).save(existingTransaction);
    }

    @Test
    void shouldRejectUpdateWhenTransactionDoesNotExist() {

        when(transactionRepository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionServiceImpl.updateTransaction(
                        99L,
                        TransactionType.EXPENSE,
                        new BigDecimal("5000.00"),
                        "Groceries",
                        LocalDate.now(),
                        1L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction not found");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldRejectUpdateWhenCategoryDoesNotExist() {

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Groceries",
                LocalDate.now(),
                new Category("Groceries", TransactionType.EXPENSE)
        );

        when(transactionRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(transaction));

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionServiceImpl.updateTransaction(
                        1L,
                        TransactionType.EXPENSE,
                        new BigDecimal("6000.00"),
                        "Updated groceries",
                        LocalDate.now(),
                        99L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category not found");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldRejectUpdateWhenCategoryTypeDoesNotMatch() {

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Groceries",
                LocalDate.now(),
                new Category("Groceries", TransactionType.EXPENSE)
        );

        Category incomeCategory = new Category(
                "Salary",
                TransactionType.INCOME
        );

        when(transactionRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(transaction));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(incomeCategory));

        assertThatThrownBy(() ->
                transactionServiceImpl.updateTransaction(
                        1L,
                        TransactionType.EXPENSE,
                        new BigDecimal("6000.00"),
                        "Updated groceries",
                        LocalDate.now(),
                        2L
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction type must match category type");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldSoftDeleteTransactionSuccessfully() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.now(),
                category
        );

        when(transactionRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(transaction));

        transactionServiceImpl.deleteTransaction(1L);

        assertThat(transaction.isDeleted()).isTrue();
        assertThat(transaction.getDeletedAt()).isNotNull();

        verify(transactionRepository).save(transaction);
    }

    @Test
    void shouldRejectDeletionWhenTransactionDoesNotExist() {

        when(transactionRepository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionServiceImpl.deleteTransaction(99L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction not found");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldRejectDeletionWhenTransactionIsAlreadyDeleted() {

        when(transactionRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionServiceImpl.deleteTransaction(1L)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction not found");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldRetrieveAllActiveTransactions() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        Transaction firstTransaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Groceries",
                LocalDate.now(),
                category
        );

        Transaction secondTransaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("2000.00"),
                "Transport",
                LocalDate.now(),
                category
        );

        when(transactionRepository.findAllByDeletedFalse())
                .thenReturn(List.of(firstTransaction, secondTransaction));

        List<Transaction> result = transactionServiceImpl.getAllTransactions();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(firstTransaction, secondTransaction);

        verify(transactionRepository).findAllByDeletedFalse();
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsExist() {

        when(transactionRepository.findAllByDeletedFalse())
                .thenReturn(List.of());

        List<Transaction> result = transactionServiceImpl.getAllTransactions();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(transactionRepository).findAllByDeletedFalse();
    }


    @Test
    void shouldRetrieveTransactionsWithPagination() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.now(),
                category
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(transaction),
                pageable,
                1
        );

        when(transactionRepository.findAllByDeletedFalse(any(Pageable.class)))
                .thenReturn(transactionPage);
        Page<Transaction> result =
                transactionServiceImpl.getAllTransactions(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isEqualTo(0);

        verify(transactionRepository).findAllByDeletedFalse(any(Pageable.class));
    }

}
