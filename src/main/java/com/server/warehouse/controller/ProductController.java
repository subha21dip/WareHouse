package com.server.warehouse.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.warehouse.exception.ProductCountException;
import com.server.warehouse.exception.ProductDetailsNotFoundException;
import com.server.warehouse.exception.ProductNotFoundException;
import com.server.warehouse.exception.SellQuantityException;
import com.server.warehouse.manager.ProductManager;
import com.server.warehouse.model.entity.Article;
import com.server.warehouse.model.entity.Product;
import com.server.warehouse.model.entity.ProductDetails;
import com.server.warehouse.model.vo.ErrorResponseBodyVO;
import com.server.warehouse.model.vo.ProductArticlesVO;
import com.server.warehouse.model.vo.ProductVO;
import com.server.warehouse.model.vo.ProductsVO;
import com.server.warehouse.repository.ArticleRepository;
import com.server.warehouse.repository.ProductDetailsRepository;
import com.server.warehouse.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    ProductDetailsRepository productDetailsRepository;
    ProductManager manager = new ProductManager();

    private final String FAILURE_STATUS = "fail";

    /*
    Get product list as a json file
    According to articles available in the inventory,
    calculate the product quantity and save to db
    */
    @PostMapping("/v1/products")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getOriginalFilename();
        logger.info("Got product file upload request with file name: " + name + " using API : /v1/products");
        try {
            ProductsVO productsVO = new ObjectMapper().readValue(multipartFile.getBytes(), ProductsVO.class);
            logger.debug("find " + productsVO.getProducts().size() + " product details in file : " + name);
            List<Article> articles = articleRepository.findAll();
            logger.debug("find " + articles.size() + " article details in db : " + name);
            List<Product> productList = manager.calculateProductQuantity(productsVO, articles);
            //When we get a valid product entity list with updated quantity, delete all existing products and add all new products
            // and also add product article details in ProductDetails table
            if (productList != null && !productList.isEmpty()) {
                productRepository.deleteAll();
                productRepository.saveAll(productList);
                List<ProductDetails> productDetails = new ArrayList<ProductDetails>();
                for (ProductVO productVO : productsVO.getProducts()) {
                    for (ProductArticlesVO articleVO : productVO.getArticles()) {
                        productDetails.add(new ProductDetails(productVO.getName(), articleVO.getId(), articleVO.getQuantity()));
                    }
                }
                productDetailsRepository.saveAll(productDetails);
                return new ResponseEntity<>(productsVO.getProducts().size() + " products uploaded successfully", HttpStatus.OK);
            }else
                throw new ProductCountException("Got exception calculating product quantity");

        }catch (JsonParseException jpe){
            logger.error(jpe.getMessage() + " when uploading product information");
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, jpe.getMessage()), HttpStatus.BAD_REQUEST);
        }catch(ProductCountException pce){
            logger.error("Got exception when calculating product quantity for file : " + name);
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, pce.getMessage()), HttpStatus.NOT_MODIFIED);
        }catch (Exception e){
            logger.error("Got exception " + e.getMessage() + " when uploading product file : " + name);
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Fetch all the products present in the warehouse currently
    with product name, price and quantity
    */
    @GetMapping("/v1/products")
    public ResponseEntity<List<?>> getAllProducts() {
        logger.info("Got get request for all product details with article information using API /v1/products");
        try {
            List<Product> products = new ArrayList<Product>();
            List<ProductVO> productVOList = new ArrayList<ProductVO>();
            productRepository.findAll().forEach(products::add);
            if (products.isEmpty()) {
                throw new ProductNotFoundException("No product found in the warehouse");
            }
            logger.debug("Fetched " + products.size() + " product information from db");
            for (Product product: products) {
                String name = product.getName();
                List<ProductDetails> detailsList = new ArrayList<>();
                List<ProductArticlesVO> productArticleList = new ArrayList<>();
                detailsList = productDetailsRepository.findByProductName(name);
                logger.debug("For" + name + " find " + detailsList.size() + " article information from db");
                if (detailsList.isEmpty()) {
                    throw new ProductDetailsNotFoundException("No product details found in the warehouse for the product " + name);
                }
                for (ProductDetails details: detailsList) {
                    productArticleList.add(new ProductArticlesVO(details.getArticleId(), details.getQuantity()));
                }
                productVOList.add(new ProductVO(name, product.getPrice(), productArticleList));
            }
            return new ResponseEntity<>(productVOList, HttpStatus.OK);
        }catch (ProductNotFoundException pne) {
            List<Object> responseList = new ArrayList<Object>();
            responseList.add(new ErrorResponseBodyVO(FAILURE_STATUS, pne.getMessage()));
            logger.error("Got exception" + pne.getMessage() + " when fetching product details");
            return new ResponseEntity<>( responseList, HttpStatus.NO_CONTENT);
        }catch (ProductDetailsNotFoundException pdne) {
            List<Object> responseList = new ArrayList<Object>();
            responseList.add(new ErrorResponseBodyVO(FAILURE_STATUS, pdne.getMessage()));
            logger.error("Got exception" + pdne.getMessage() + " when fetching product details");
            return new ResponseEntity<>( responseList, HttpStatus.NO_CONTENT);
        }
        catch (Exception e) {
            List<Object> responseList = new ArrayList<Object>();
            responseList.add(new ErrorResponseBodyVO(FAILURE_STATUS, e.getMessage()));
            logger.error("Got exception" + e.getMessage() + " when requesting for all the products");
            return new ResponseEntity<>(responseList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Delete a product by name
    */
    @DeleteMapping("/v1/products/{name}")
    public ResponseEntity<?> deleteProduct(@PathVariable("name") String name) {
        logger.info("Got delete request for product " + name + " with api call : /v1/products/{name}");
        try {
            productRepository.deleteByName(name);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Got exception" + e.getMessage() + "when deleting " + name);
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Update product quantity through this API, when some unit of the product is sold
    */
    @PutMapping("/v1/products/{name}")
    public ResponseEntity<?> sellProduct(@PathVariable("name") String name,
                                                    @RequestParam(required = true) int sellUnit) {
        logger.info("Got product quantity update request for product : " + name + " and quantity " + sellUnit);
        try {
            Optional<Product> product = productRepository.findByName(name);
            if(product.isPresent()){
                Product productEntity = product.get();
                if(sellUnit > productEntity.getCount())
                    //Sell quantity is more than current quantity in stock, reject the request
                    throw new SellQuantityException("Sell quantity" + sellUnit + "not available in the inventory for product :" + name);
                else{
                    //update each inventory items quantity
                    List<ProductDetails> productDetailsList = new ArrayList<ProductDetails>();
                    productDetailsRepository.findByProductName(name).forEach(productDetailsList::add);
                    logger.debug("Found " + productDetailsList.size() +" articles details for product " + name);
                    for (ProductDetails productDetails: productDetailsList
                         ) {
                        Optional<Article> article = articleRepository.findById(productDetails.getArticleId());
                        if(article.isPresent()){
                            Article articleEntity = article.get();
                            Long newCount = articleEntity.getCount() - (sellUnit * productDetails.getQuantity());
                            logger.debug("Updated article " + articleEntity.getName() + " quantity by " + (sellUnit * productDetails.getQuantity()));
                            articleEntity.setCount(newCount);
                        }
                    }
                    // update product quantity
                    long updatedUnit = productEntity.getCount() - sellUnit;
                    productEntity.setCount(updatedUnit);
                    return new ResponseEntity<>(productRepository.save(productEntity), HttpStatus.OK);
                }
            }else
                throw new ProductNotFoundException("No product found with provided product name");

        }catch (SellQuantityException sqe) {
            logger.error(" Got error " + sqe.getMessage()+ " from update product request");
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, sqe.getMessage()), HttpStatus.NOT_MODIFIED);
        }catch (ProductNotFoundException pne) {
            logger.error(" Got error " + pne.getMessage()+ " from update product request");
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, pne.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            logger.error(" Got error " + e.getMessage()+ " from update product request");
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
