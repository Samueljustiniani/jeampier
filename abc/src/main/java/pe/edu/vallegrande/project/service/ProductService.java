package pe.edu.vallegrande.project.service;

import pe.edu.vallegrande.project.model.Products;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Products> findAll();  

    Optional<Products> findById(Long id);  

    Products save(Products product);  

    Products update(Products product);  

    void delete(Long id); 
}
