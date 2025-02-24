package com.adobe.bookstore.controller;

import com.adobe.bookstore.dto.StockUpdateDTO;
import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.service.BookStockService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing book stock in the bookstore.
 */
@RestController
@RequestMapping("/books_stock/")
public class BookStockResource {

  private static final Logger LOGGER = LogManager.getLogger();
  @Autowired
  private BookStockRepository bookStockRepository;
  @Autowired
  private BookStockService bookStockService;

  /**
   * Retrieves stock information for a specific book by its ID.
   *
   * @param bookId The ID of the book.
   * @return A response containing the stock information or a 404 if not found.
   */
  @GetMapping("{bookId}")
  public ResponseEntity<BookStock> getStockById(@PathVariable String bookId) {
    return bookStockRepository.findById(bookId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Updates the stock quantity for a specific book.
   *
   * @param bookId      The ID of the book.
   * @param stockUpdate The stock update details.
   * @return A response indicating the result of the update.
   */
  @PatchMapping("{bookId}/update")
  public ResponseEntity<Void> updateStock(@PathVariable String bookId,
      StockUpdateDTO stockUpdate) {
    try {
      bookStockService.updateStock(bookId, stockUpdate.getUpdateQuantity());
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      LOGGER.error("Resource not found {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }
}
