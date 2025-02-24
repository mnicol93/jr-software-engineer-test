package com.adobe.bookstore.model.order;

import com.adobe.bookstore.model.order.constants.OrderDetailStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the details of an order, where each line item corresponds to a book and its quantity.
 * <p>
 * This class contains the following:
 * <ul>
 *     <li>A primary key (id) to uniquely identify the order detail.</li>
 *     <li>A foreign key (book_id) linking to the Book entity to fetch the book name.</li>
 *     <li>The quantity of the book in the order, which must be less than or equal to the available stock (BookStock.quantity).</li>
 *     <li>A foreign key (order_id) linking the order detail to a specific order. Multiple order details can share the same order_id.</li>
 * </ul>
 * </p>
 * <p>
 * The OrderDetail class allows an order to consist of multiple books, each with its own quantity.
 * </p>
 */

@Entity
@Table(name = "order_detail")
@Setter
@Getter
public class OrderDetail {

  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "order_id", nullable = true)
  private UUID orderId;

  @Column(name = "book_id", nullable = false)
  private String bookId;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "status", nullable = true)
  private OrderDetailStatus status;
}
