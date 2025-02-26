package com.adobe.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Data Transfer Object representing a stock update request.
 */
@Getter
public class StockUpdateDTO {

  private final int updateQuantity;

  /**
   * Constructs a StockUpdateDTO.
   *
   * @param updateQuantity The quantity to update the stock by.
   */
  public StockUpdateDTO(@JsonProperty("quantity") final int updateQuantity) {
    this.updateQuantity = updateQuantity;
  }
}
