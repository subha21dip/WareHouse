package com.server.warehouse.controller;

import com.server.warehouse.model.entity.Article;
import com.server.warehouse.model.entity.ProductDetails;
import com.server.warehouse.repository.ArticleRepository;
import com.server.warehouse.repository.ProductDetailsRepository;
import com.server.warehouse.repository.ProductRepository;
import com.server.warehouse.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductRepository productRepository;
    ArticleRepository articleRepository;
    ProductDetailsRepository productDetailsRepository;

    /*
    Fetch all the products present in the warehouse currently
    with product name, price and quantity
    */
    @GetMapping("/v1/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = new ArrayList<Product>();
            productRepository.findAll().forEach(products::add);

            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Delete a product by id
    */
    @DeleteMapping("/v1/products/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") long id) {
        try {
            productRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Update product quantity through this API, when some unit of the product is sold
    */
    @PutMapping("/v1/products/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id,
                                                    @RequestParam(required = true) int sellUnit) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if(product.isPresent()){
                Product _product = product.get();
                if(sellUnit > _product.getCount())
                    //Sell quantity is more than current quantity in stock, reject the request
                    return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
                else{
                    //update each inventory items quantity
                    List<ProductDetails> productDetailsList = new ArrayList<ProductDetails>();
                    productDetailsRepository.findAllBYProductId(id).forEach(productDetailsList::add);
                    for (ProductDetails _productDetails: productDetailsList
                         ) {
                        Optional<Article> article = articleRepository.findById(_productDetails.getArticleId());
                        if(article.isPresent()){
                            Article _article = article.get();
                            Long newCount = _article.getCount() - (sellUnit * _productDetails.getQuantity());
                            _article.setCount(newCount);
                        }
                    }
                    // update product quantity
                    long updatedUnit = _product.getCount() - sellUnit;
                    _product.setCount(updatedUnit);
                    return new ResponseEntity<>(productRepository.save(_product), HttpStatus.OK);
                }
            }else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
