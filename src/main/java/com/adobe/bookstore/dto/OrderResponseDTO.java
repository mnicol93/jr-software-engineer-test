package com.adobe.bookstore.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data Transfer Object representing the response for a created order.
 */
@AllArgsConstructor
@Getter
public class OrderResponseDTO {

  /**
   * Constructs an OrderResponseDTO.
   *
   * @param id The ID of the created order.
   */
  private UUID id;
}
