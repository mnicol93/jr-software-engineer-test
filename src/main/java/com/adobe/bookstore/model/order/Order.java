package com.adobe.bookstore.model.order;

import com.adobe.bookstore.model.order.constants.Status;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an order in the system. This entity is used to store order data in the database and
 * includes order details, date, and status.
 */
@Entity
@Table(name = "orders")
@JsonSerialize
@Setter
@Getter
public class Order {

  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @OneToMany(mappedBy = "orderId")
  private List<OrderDetail> orderDetails;

  @Column(name = "date", nullable = false)
  private Date date;

  @Column(name = "status", nullable = false)
  private Status status;
}

