package ng.expensetracker.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.dto.request.CreateTransactionRequest;
import ng.expensetracker.dto.request.UpdateTransactionRequest;
import ng.expensetracker.dto.response.TransactionResponse;
import ng.expensetracker.mapper.TransactionMapper;
import ng.expensetracker.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(
                request.type(),
                request.amount(),
                request.description(),
                request.transactionDate(),
                request.categoryId()
        );

        TransactionResponse response = TransactionMapper.toResponse(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id
    ) {
        Transaction transaction = transactionService.getTransaction(id);

        return ResponseEntity.ok(
                TransactionMapper.toResponse(transaction)
        );
    }

    // GET ALL (PAGINATED)
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            Pageable pageable
    ) {
        Page<TransactionResponse> transactions =
                transactionService.getAllTransactions(pageable)
                        .map(TransactionMapper::toResponse);

        return ResponseEntity.ok(transactions);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request
    ) {
        Transaction transaction = transactionService.updateTransaction(
                id,
                request.type(),
                request.amount(),
                request.description(),
                request.transactionDate(),
                request.categoryId()
        );

        return ResponseEntity.ok(
                TransactionMapper.toResponse(transaction)
        );
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id
    ) {
        transactionService.deleteTransaction(id);

        return ResponseEntity.noContent().build();
    }
}
