package com.adobe.bookstore.service;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing book stock in the bookstore.
 */
@Service
public class BookStockService {

  @Autowired
  private BookStockRepository bookStockRepository;

  /**
   * Updates the stock of a specific book by adjusting its quantity.
   *
   * @param bookId    The ID of the book to update.
   * @param qtyChange The quantity change to apply.
   */
  @Transactional
  public void updateStock(String bookId, int qtyChange) {
    BookStock book = bookStockRepository.findById(bookId).orElseThrow(
        () -> new RuntimeException("Book not found")
    );
    book.setQuantity(book.getQuantity() + qtyChange);
    bookStockRepository.save(book);
  }
}
