package com.server.warehouse.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.warehouse.exception.ArticleNotFoundException;
import com.server.warehouse.manager.ProductManager;
import com.server.warehouse.model.entity.Article;
import com.server.warehouse.model.vo.ArticleVO;
import com.server.warehouse.model.vo.ErrorResponseBodyVO;
import com.server.warehouse.model.vo.InventoryVO;
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

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ArticleRepository articleRepository;

    private final String FAILURE_STATUS = "fail";

    /*
    Get inventory items as a json file,
    Update the articles count in db
    */
    @PostMapping("/v1/inventory")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getOriginalFilename();
        try {
            logger.info("Got inventory file upload request with file name: " + name + " using API : /v1/inventory");
            InventoryVO inventoryVO = new ObjectMapper().readValue(multipartFile.getBytes(), InventoryVO.class);
            logger.debug("find " + inventoryVO.getInventory().size() + " article details in file : " + name);
            List<ArticleVO> articleVOList = inventoryVO.getInventory();
            List<Article> articles = new ArrayList<Article>();
            for (ArticleVO articleVO : articleVOList) {
                articles.add(new Article(articleVO.getId(), articleVO.getName(), articleVO.getQuantity()));
            }
            articleRepository.saveAll(articles);
            return new ResponseEntity<>(inventoryVO.getInventory().size() + " inventory items uploaded successfully", HttpStatus.OK);
        }catch (JsonParseException jpe){
            logger.error(jpe.getMessage() + " when uploading inventory information");
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, jpe.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error("Got exception " + e.getMessage() + " when uploading inventory file : " + name);
            return new ResponseEntity<>(new ErrorResponseBodyVO(FAILURE_STATUS, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Fetch all the articles present in the inventory
    with product name and quantity
    */
    @GetMapping("/v1/inventory")
    public ResponseEntity<List<?>> getAllArticles() {
        logger.info("Got get request for all inventory article details using API /v1/inventory");
        try {
            List<Article> articles = new ArrayList<Article>();
            articleRepository.findAll().forEach(articles::add);
            if (articles.isEmpty()) {
                throw new ArticleNotFoundException("No article found in the inventory");
            }
            logger.debug("Fetched " + articles.size() + " article information from db");
            return new ResponseEntity<>(articles, HttpStatus.OK);
        }catch (ArticleNotFoundException pne) {
            List<Object> responseList = new ArrayList<Object>();
            responseList.add(new ErrorResponseBodyVO(FAILURE_STATUS, pne.getMessage()));
            logger.error("Got exception" + pne.getMessage() + " when fetching inventory details");
            return new ResponseEntity<>( responseList, HttpStatus.NO_CONTENT);
        }catch (Exception e) {
            List<Object> responseList = new ArrayList<Object>();
            responseList.add(new ErrorResponseBodyVO(FAILURE_STATUS, e.getMessage()));
            logger.error("Got exception" + e.getMessage() + " when requesting for all the inventory articles");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
