package com.adobe.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.adobe")
public class BookstoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(BookstoreApplication.class, args);
  }

}
