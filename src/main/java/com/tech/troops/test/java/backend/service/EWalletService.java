package com.tech.troops.test.java.backend.service;

import com.tech.troops.test.java.backend.entity.User;
import com.tech.troops.test.java.backend.entity.Transaction;
import com.tech.troops.test.java.backend.repository.TransactionRepository;
import com.tech.troops.test.java.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
/**
 * // TODO Comment
 */
@Service
public class EWalletService {

  private static final Logger logger = LoggerFactory.getLogger(EWalletService.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  @Transactional
  public synchronized Transaction credit(Long userId, BigDecimal amount) throws IllegalArgumentException {
    logger.info("Processing credit transaction for user_id: {}, amount: {}", userId, amount);
    
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      logger.error("Invalid amount for credit transaction: {}", amount);
      throw new IllegalArgumentException("Invalid amount");
    }

    User user = userRepository.findById(userId).orElseThrow(() -> {
      logger.error("User not found for user_id: {} in credit method", userId);
      return new RuntimeException("User not found");
    });
    user.setBalance(user.getBalance().add(amount));

    Transaction transaction = new Transaction();
    transaction.setUser(user);
    transaction.setAmount(amount);
    transaction.setType("credit");

    userRepository.save(user);
    Transaction savedTransaction = transactionRepository.save(transaction);

    logger.info("Credit transaction successful for user_id: {}, transaction_id: {}", userId, savedTransaction.getId());
    return savedTransaction;
  }

  @Transactional
  public synchronized Transaction debit(Long userId, BigDecimal amount) throws IllegalArgumentException, RuntimeException {
    logger.info("Processing debit transaction for user_id: {}, amount: {}", userId, amount);
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      logger.error("Invalid amount for debit transaction: {}", amount);
      throw new IllegalArgumentException("Invalid amount");
    }

    User user = userRepository.findById(userId).orElseThrow(() -> {
      logger.error("User not found for user_id: {} in debit method", userId);
      return new RuntimeException("User not found");
    });
    if (user.getBalance().compareTo(amount) < 0) {
      logger.error("Insufficient funds for user_id: {}, current_balance: {}, requested_amount: {}", userId, user.getBalance(), amount);
      throw new RuntimeException("Insufficient funds");
    }

    user.setBalance(user.getBalance().subtract(amount));

    Transaction transaction = new Transaction();
    transaction.setUser(user);
    transaction.setAmount(amount);
    transaction.setType("debit");

    userRepository.save(user);
    Transaction savedTransaction = transactionRepository.save(transaction);

    logger.info("Debit transaction successful for user_id: {}, transaction_id: {}", userId, savedTransaction.getId());
    return savedTransaction;
  }

  public BigDecimal getUserBalance(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> {
      logger.error("User not found for user_id: {} in getUserBalance method", userId);
      return new RuntimeException("User not found");
    });
    return user.getBalance();
  }
}
