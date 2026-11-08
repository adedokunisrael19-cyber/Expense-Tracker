package ng.expensetracker.data.repository;

import ng.expensetracker.data.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndDeletedFalse(Long id);
    List<Transaction> findAllByDeletedFalse();
    Page<Transaction> findAllByDeletedFalse(Pageable pageable);
    boolean existsByCategoryId(Long categoryId);

    Object existsByCategory_Id(long l);
}

