package com.server.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.warehouse.manager.ProductManager;
import com.server.warehouse.model.entity.Article;
import com.server.warehouse.model.entity.ProductDetails;
import com.server.warehouse.model.vo.ArticleVO;
import com.server.warehouse.model.vo.ProductArticlesVO;
import com.server.warehouse.model.vo.ProductVO;
import com.server.warehouse.model.vo.ProductsVO;
import com.server.warehouse.repository.ArticleRepository;
import com.server.warehouse.repository.ProductDetailsRepository;
import com.server.warehouse.repository.ProductRepository;
import com.server.warehouse.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    ProductDetailsRepository productDetailsRepository;
    ProductManager manager = new ProductManager();

    /*
    Get product list as a json file
    According to articles available in the inventory,
    calculate the product quantity and save to db
    */
    @PostMapping("/v1/products")
    public String handleFileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getOriginalFilename();
        System.out.println("File name: "+name);
        ProductsVO productsVO = new ObjectMapper().readValue(multipartFile.getBytes(), ProductsVO.class);
        List<Article> articles = articleRepository.findAll();
        List<Product> productList = manager.calculateProductQuantity(productsVO, articles);
        //When we get a valid product entity list with updated quantity, delete all existing products and add all new products
        // and also add product article details in ProductDetails table
        if(productList != null && !productList.isEmpty()) {
            productRepository.deleteAll();
            productRepository.saveAll(productList);
            List<ProductDetails> productDetails = new ArrayList<ProductDetails>();
            for (ProductVO productVO: productsVO.getProducts()) {
                for (ProductArticlesVO articleVO: productVO.getArticles()) {
                    productDetails.add(new ProductDetails(productVO.getName(), articleVO.getId(), articleVO.getQuantity()));
                }
            }
            productDetailsRepository.saveAll(productDetails);
        }
        return productsVO.getProducts().size() + " products uploaded successfully";
    }

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
    Delete a product by name
    */
    @DeleteMapping("/v1/products/{name}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("name") String name) {
        try {
            productRepository.deleteByName(name);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Update product quantity through this API, when some unit of the product is sold
    */
    @PutMapping("/v1/products/{name}")
    public ResponseEntity<Product> sellProduct(@PathVariable("name") String name,
                                                    @RequestParam(required = true) int sellUnit) {
        try {
            Optional<Product> product = productRepository.findByName(name);
            if(product.isPresent()){
                Product _product = product.get();
                if(sellUnit > _product.getCount())
                    //Sell quantity is more than current quantity in stock, reject the request
                    return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
                else{
                    //update each inventory items quantity
                    List<ProductDetails> productDetailsList = new ArrayList<ProductDetails>();
                    productDetailsRepository.findByProductName(name).forEach(productDetailsList::add);
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
