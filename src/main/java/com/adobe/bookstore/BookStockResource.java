package com.adobe.bookstore;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books_stock/")
public class BookStockResource {
    private static final Logger LOGGER = LogManager.getLogger();

    private BookStockRepository bookStockRepository;

    @Autowired
    public BookStockResource(BookStockRepository bookStockRepository) {
        this.bookStockRepository = bookStockRepository;
    }

    @GetMapping("{bookId}")
    public ResponseEntity<BookStock> getStockById(@PathVariable String bookId) {
        return bookStockRepository.findById(bookId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    LOGGER.error("Can't find order with orderId: " + bookId);
                    return new OrderNotFoundException(bookId);
                });
    }
}
