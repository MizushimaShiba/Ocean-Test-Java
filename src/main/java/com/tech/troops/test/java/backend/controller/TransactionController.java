package com.tech.troops.test.java.backend.controller;

import com.tech.troops.test.java.backend.service.EWalletService;
import com.tech.troops.test.java.backend.entity.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * // TODO Comment
 */

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

  private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

  @Autowired
  private EWalletService eWalletService;

  @PostMapping("/credit")
  public ResponseEntity<Map<String, Object>> credit(@RequestBody Map<String, Object> requestBody) {
    Long userId = Long.valueOf(requestBody.get("user_id").toString());
    BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());

    logger.info("Received credit request for user_id: {}, amount: {}", userId, amount);

    Map<String, Object> response = new HashMap<>();
    try {
      Transaction transaction = eWalletService.credit(userId, amount);
      response.put("status", "success");
      response.put("transaction_id", transaction.getId());
      response.put("new_balance", eWalletService.getUserBalance(userId));
      logger.info("Credit transaction completed for user_id: {}, transaction_id: {}", userId, transaction.getId());
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      response.put("status", "error");
      response.put("message", e.getMessage());
      logger.error("Error processing credit transaction for user_id: {}: {}", userId, e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/debit")
  public ResponseEntity<Map<String, Object>> debit(@RequestBody Map<String, Object> requestBody) {
    Long userId = Long.valueOf(requestBody.get("user_id").toString());
    BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());

    logger.info("Received debit request for user_id: {}, amount: {}", userId, amount);

    Map<String, Object> response = new HashMap<>();
    try {
      Transaction transaction = eWalletService.debit(userId, amount);
      response.put("status", "success");
      response.put("transaction_id", transaction.getId());
      response.put("new_balance", eWalletService.getUserBalance(userId));
      logger.info("Debit transaction completed for user_id: {}, transaction_id: {}", userId, transaction.getId());
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      response.put("status", "error");
      response.put("message", e.getMessage());
      logger.error("Error processing debit transaction for user_id: {}: {}", userId, e.getMessage());
      return ResponseEntity.badRequest().body(response);
    } catch (RuntimeException e) {
      response.put("status", "error");
      response.put("message", e.getMessage());
      logger.error("Error processing debit transaction for user_id: {}: {}", userId, e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }
}
