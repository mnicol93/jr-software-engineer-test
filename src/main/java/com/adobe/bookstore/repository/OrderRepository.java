package com.adobe.bookstore.repository;

import com.adobe.bookstore.model.order.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link Order} entities. Extends
 * {@link JpaRepository} to provide basic CRUD functionality and custom query methods.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

}
