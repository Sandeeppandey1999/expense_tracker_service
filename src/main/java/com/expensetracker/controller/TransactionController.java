package com.expensetracker.controller;
import com.expensetracker.model.Transaction;
import com.expensetracker.security.JwtUtil;
import com.expensetracker.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private JwtUtil jwtUtil;

 
    private String extractUsernameFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.extractUsername(token);
        }
        return null;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Transaction>> getAllTransactions(HttpServletRequest request) {
        String username = extractUsernameFromRequest(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Transaction> transactions = transactionService.getTransactionsByUsername(username);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/add")
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction, HttpServletRequest request) {
        String username = extractUsernameFromRequest(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        transaction.setUsername(username);
        Transaction savedTransaction = transactionService.addTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id, HttpServletRequest request) {
        String username = extractUsernameFromRequest(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        transactionService.deleteTransactionByUser(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Double>> getStats(HttpServletRequest request) {
        String username = extractUsernameFromRequest(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        double income = transactionService.getTotalIncome(username);
        double expense = transactionService.getTotalExpense(username);
        double balance = income - expense;

        Map<String, Double> stats = new HashMap<>();
        stats.put("income", income);
        stats.put("expense", expense);
        stats.put("balance", balance);

        return ResponseEntity.ok(stats);
    }
} 

