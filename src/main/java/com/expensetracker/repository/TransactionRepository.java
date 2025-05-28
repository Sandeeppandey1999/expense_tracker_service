package com.expensetracker.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.expensetracker.model.Transaction;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUsername(String username);
    void deleteByIdAndUsername(Long id, String username);
}
