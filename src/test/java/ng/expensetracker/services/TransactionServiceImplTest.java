package ng.expensetracker.services;

import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.TransactionType;
import ng.expensetracker.data.repository.CategoryRepository;
import ng.expensetracker.data.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

        Category incomeCategory = new Category("Salary", TransactionType.INCOME
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(incomeCategory));

        assertThatThrownBy(() ->
                transactionServiceImpl.createTransaction(
                        TransactionType.EXPENSE, new BigDecimal("5000.00"),
                        "Groceries", LocalDate.now(), 1L
                )
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction type must match category type");

        verify(transactionRepository, never()).save(any());
    }

}
