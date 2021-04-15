package com.server.warehouse.controllerTest;

import com.server.warehouse.controller.ProductController;
import com.server.warehouse.model.entity.Product;
import com.server.warehouse.model.entity.ProductDetails;
import com.server.warehouse.repository.ArticleRepository;
import com.server.warehouse.repository.ProductDetailsRepository;
import com.server.warehouse.repository.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    ProductRepository productRepository;
    @MockBean
    ArticleRepository articleRepository;
    @MockBean
    ProductDetailsRepository productDetailsRepository;

    @Test
    public void returnProductsWhenGetProductsCalled()
            throws Exception {
        Product chair = new Product("chair", 500.00, 10, 1 );
        ProductDetails detail = new ProductDetails("chair", 1L, 10L);

        List<Product> allProducts = Arrays.asList(chair);
        List<ProductDetails> productDetailsList = Arrays.asList(detail);

        given(productRepository.findAll()).willReturn(allProducts);
        given(productDetailsRepository.findByProductName("chair")).willReturn(productDetailsList);

        mvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(chair.getName())));
    }
}
