package com.tech.troops.test.java.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * // TODO Comment
 */

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private String type;  // "credit" or "debit"

  @CreationTimestamp
  private LocalDateTime createdAt;
  
}
