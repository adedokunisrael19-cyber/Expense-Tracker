package ng.expensetracker.controllers;

import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.data.model.TransactionType;
import ng.expensetracker.services.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.util.ReflectionTestUtils;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.of(2026, 11, 5),
                category
        );

        ReflectionTestUtils.setField(transaction, "id", 1L);
    }

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        Transaction transaction = new Transaction(
                TransactionType.EXPENSE,
                new BigDecimal("5000.00"),
                "Weekly groceries",
                LocalDate.of(2026, 11, 5),
                category
        );

        when(transactionService.createTransaction(
                any(TransactionType.class),
                any(BigDecimal.class),
                anyString(),
                any(LocalDate.class),
                anyLong()
        )).thenReturn(transaction);
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "type": "EXPENSE",
                                    "amount": 5000.00,
                                    "description": "Weekly groceries",
                                    "transactionDate": "2026-11-05",
                                    "categoryId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.description")
                        .value("Weekly groceries"))
                .andExpect(jsonPath("$.categoryName")
                        .value("Groceries"));
    }
    @Test
    void shouldGetTransactionById() throws Exception {

        when(transactionService.getTransaction(1L))
                .thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.description")
                        .value("Weekly groceries"));
    }

    @Test
    void shouldGetAllTransactions() throws Exception {

        Page<Transaction> transactionPage = new PageImpl<>(
                List.of(transaction),
                PageRequest.of(0, 10),
                1
        );

        when(transactionService.getAllTransactions(any(Pageable.class)))
                .thenReturn(transactionPage);

        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].description")
                        .value("Weekly groceries"));
    }

}