package com.adobe.bookstore.service;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service for managing book stock in the bookstore.
 */
@Service
public class BookStockService {

  private static final Logger LOGGER = LogManager.getLogger();
  @Autowired
  private BookStockRepository bookStockRepository;

  /**
   * Updates the stock of a specific book by decreasing its quantity.
   *
   * @param bookId     The ID of the book to update.
   * @param decreaseBy The quantity to decrease.
   */
  @Transactional
  public ResponseEntity<Void> decreaseStock(String bookId, int decreaseBy) {
    if (decreaseBy > 0) {
      try {
        BookStock book = bookStockRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException(bookId));

        if (book.getQuantity() < decreaseBy) {
          LOGGER.error("Insufficient stock for book {}", bookId);
          return ResponseEntity.unprocessableEntity().build(); // HTTP 422
        }
        book.setQuantity(book.getQuantity() - decreaseBy);
        bookStockRepository.save(book);
        return ResponseEntity.ok().build();

      } catch (RuntimeException e) {
        LOGGER.error("Book {} not found", bookId);
        return ResponseEntity.notFound().build();
      }
    }
    LOGGER.error("Quantity cannot be negative for book {}", bookId);
    return ResponseEntity.badRequest().build();
  }

  public ResponseEntity<BookStock> getStockById(String id) {
    return bookStockRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
