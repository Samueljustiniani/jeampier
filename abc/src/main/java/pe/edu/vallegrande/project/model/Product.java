package pe.edu.vallegrande.project.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "Products")
public class Product {

    @Id
    @Column(name = "product_id")
    private int productId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 8, scale = 2)
    private double unitPrice;

    @Column(name = "stocks", nullable = false, length = 300)
    private String stocks;

    @Column(name = "category_id", nullable = false)
    private int categoryId;

    @Column(name = "Detail_Sale_id_detail", nullable = false)
    private int detailSaleIdDetail;
}
