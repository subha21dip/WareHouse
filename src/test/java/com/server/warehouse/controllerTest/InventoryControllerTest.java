package com.server.warehouse.controllerTest;

import com.server.warehouse.controller.InventoryController;
import com.server.warehouse.controller.ProductController;
import com.server.warehouse.model.entity.Article;
import com.server.warehouse.repository.ArticleRepository;
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
@WebMvcTest(value = InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    ArticleRepository articleRepository;

    @Test
    public void returnProductsWhenGetProductsCalled()
            throws Exception {
        Article art = new Article(1L, "seat", 15L );
        List<Article> allArticles = Arrays.asList(art);

        given(articleRepository.findAll()).willReturn(allArticles);

        mvc.perform(get("/api/v1/inventory")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(art.getName())));
    }
}
