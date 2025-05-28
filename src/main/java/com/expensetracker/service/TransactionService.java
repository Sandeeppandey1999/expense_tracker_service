package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.expensetracker.model.Transaction;
import com.expensetracker.repository.TransactionRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsByUsername(String username) {
        return transactionRepository.findByUsername(username);
    }

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
 
    @Transactional
    public void deleteTransactionByUser(Long id, String username) {
        transactionRepository.deleteByIdAndUsername(id, username);
    }

    public double getTotalIncome(String username) {
        return transactionRepository.findByUsername(username).stream()
                .filter(t -> t.getAmount() > 0)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense(String username) {
        return transactionRepository.findByUsername(username).stream()
                .filter(t -> t.getAmount() < 0)
                .mapToDouble(t -> Math.abs(t.getAmount()))
                .sum();
    }
}
