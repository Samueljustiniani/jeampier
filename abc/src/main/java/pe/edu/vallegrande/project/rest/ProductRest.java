package pe.edu.vallegrande.project.rest;

import pe.edu.vallegrande.project.model.Products;
import pe.edu.vallegrande.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/products")
public class ProductRest {

    private final ProductService productService;

    @Autowired
    public ProductRest(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Products> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Products> findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping("/save")
    public Products save(@RequestBody Products product) {
        return productService.save(product);
    }

    @PutMapping("/update")
    public Products update(@RequestBody Products product) {
        return productService.update(product);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.delete(id);  
        return "Producto eliminado con ID: " + id;
    }
}
