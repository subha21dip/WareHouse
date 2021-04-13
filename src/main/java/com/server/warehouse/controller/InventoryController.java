package com.server.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.warehouse.manager.ProductManager;
import com.server.warehouse.model.entity.Article;
import com.server.warehouse.model.entity.Product;
import com.server.warehouse.model.vo.ArticleVO;
import com.server.warehouse.model.vo.InventoryVO;
import com.server.warehouse.repository.ArticleRepository;
import com.server.warehouse.repository.ProductDetailsRepository;
import com.server.warehouse.repository.ProductRepository;
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

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    ProductDetailsRepository productDetailsRepository;
    ProductManager manager = new ProductManager();


    /*
    Get inventory items as a json file,
    Update the articles count in db
    */
    @PostMapping("/v1/inventory")
    public String handleFileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getOriginalFilename();
        System.out.println("File name: "+name);
        InventoryVO inventoryVO = new ObjectMapper().readValue(multipartFile.getBytes(), InventoryVO.class);
        List<ArticleVO> articleVOList = inventoryVO.getInventory();
        List<Article> articles = new ArrayList<Article>();
        for (ArticleVO articleVO: articleVOList) {
            articles.add(new Article(articleVO.getId(), articleVO.getName(), articleVO.getQuantity()));
        }
        articleRepository.saveAll(articles);
        return inventoryVO.getInventory().size() + " inventory items uploaded successfully";
    }

    /*
    Fetch all the articles present in the inventory
    with product name and quantity
    */
    @GetMapping("/v1/inventory")
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            List<Article> articles = new ArrayList<Article>();
            articleRepository.findAll().forEach(articles::add);

            if (articles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(articles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
