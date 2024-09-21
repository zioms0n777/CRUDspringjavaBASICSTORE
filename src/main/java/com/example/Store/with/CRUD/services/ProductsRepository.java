package com.example.Store.with.CRUD.services;

import com.example.Store.with.CRUD.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

}
