package com.server.warehouse.manager;

import com.server.warehouse.model.entity.Article;
import com.server.warehouse.model.entity.Product;
import com.server.warehouse.model.vo.ArticleVO;
import com.server.warehouse.model.vo.ProductArticlesVO;
import com.server.warehouse.model.vo.ProductVO;
import com.server.warehouse.model.vo.ProductsVO;

import java.util.*;

public class ProductManager {

    public List<Product> calculateProductQuantity(ProductsVO productsVO, List<Article> articlesList) {
        //Convert the article list to map, It will be better for time complexity of the product quantity calculation
        Map<Long, ArticleVO> articleMap = new HashMap<Long, ArticleVO>();
        for (Article article: articlesList) {
            articleMap.put(article.getId(), new ArticleVO(article.getId(), article.getName(), article.getCount()));
        }
        List<ProductVO> productList = productsVO.getProducts();
        Collections.sort(productList);
        List<Product> products = new ArrayList<Product>();
        //If no articles are uploaded to db, put all product quantity to zero
        if(articleMap == null || articleMap.isEmpty() || articleMap.size() == 0) {
            for (ProductVO productVO : productList
            ) {
                products.add(new Product(productVO.getName(), productVO.getPrice(), 0, productVO.getPriority()));
            }
        }
        // if articles are available in the inventory, try to count product quantity according to the product priority
        else{
            for (ProductVO productVO : productList
            ) {
                // calculate the maximum quantity available for all the articles needed for the product
                List<Long> quantityList = new ArrayList<Long>();
                List<ProductArticlesVO> articles = productVO.getArticles();
                for (ProductArticlesVO productArticle : articles
                     ) {
                    try {
                        Long articleCount = articleMap.get(productArticle.getId()).getQuantity();
                        Long currentQuantity = (long) Math.floor(articleCount / productArticle.getQuantity());
                        quantityList.add(currentQuantity);
                    }catch (Exception e){
                        quantityList.add(0L);
                    }
                }
                Collections.sort(quantityList);
                Long quantity = quantityList.get(0);
                for (ProductArticlesVO productArticle : articles
                ) {
                    Long articleCount = articleMap.get(productArticle.getId()).getQuantity();
                    articleMap.get(productArticle.getId()).setQuantity(articleCount - (quantity * productArticle.getQuantity()));
                }
                products.add(new Product(productVO.getName(), productVO.getPrice(), quantity, productVO.getPriority()));
            }
        }
        return products;
    }
}
