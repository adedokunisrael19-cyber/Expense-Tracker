package ng.expensetracker.services;

import lombok.RequiredArgsConstructor;
import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.Transaction;
import ng.expensetracker.data.model.TransactionType;
import ng.expensetracker.data.repository.CategoryRepository;
import ng.expensetracker.data.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction createTransaction(TransactionType transactionType, BigDecimal amount, String description, LocalDate transactionDate, long Id) {
//        Constructor injection	Preferred for mandatory dependencies
//        Setter injection	Useful for optional or changeable dependencies
//        Field injection	Generally avoid in application code
        Category category = categoryRepository.findById(Id).orElseThrow(()-> new IllegalArgumentException("Category not found"));

        if(category.getType() != transactionType){
            throw new IllegalArgumentException("Transaction type must match category type");
        }

        Transaction transaction = new Transaction( transactionType,
                amount,
                description,
                transactionDate,
                category);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransaction(long id)   {
       return transactionRepository.findByIdAndDeletedFalse(id).orElseThrow(()-> new IllegalArgumentException("Transaction not found"));
    }

    @Override
    public Transaction updateTransaction(long transactionId, TransactionType transactionType, BigDecimal amount, String description, LocalDate transactionDate, long categoryId) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(transactionId).orElseThrow(()-> new IllegalArgumentException("Transaction not found"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new IllegalArgumentException("Category not found"));

        if(category.getType() != transactionType){
            throw new IllegalArgumentException("Transaction type must match category type");
        }
        transaction.updateDetails(transactionType,amount,description,transactionDate,category);
        return  transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(long id) {
        Transaction transaction = transactionRepository.findByIdAndDeletedFalse(id).orElseThrow(()-> new IllegalArgumentException("Transaction not found"));

        transaction.softDelete();
        transactionRepository.save(transaction);

    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByDeletedFalse();
    }

       @Override
       public Page<Transaction> getAllTransactions(Pageable pageable) {
       return transactionRepository.findAllByDeletedFalse(pageable);
   }

}
