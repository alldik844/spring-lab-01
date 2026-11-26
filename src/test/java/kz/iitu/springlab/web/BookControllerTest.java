
package kz.iitu.springlab.web;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService service;

    @Test
    @WithMockUser(roles = "USER")
    void existingBookReturns200() throws Exception {

        Book book = new Book(
                1L,
                "Dune",
                "Frank Herbert",
                1965
        );

        given(service.findById(1L))
                .willReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Dune"))
                .andExpect(jsonPath("$.author")
                        .value("Frank Herbert"));

        verify(service).findById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void missingBookReturns404() throws Exception {

        given(service.findById(999L))
                .willReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Book not found: 999"));

        verify(service).findById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidBookReturns400() throws Exception {

        String invalidJson = """
                {
                    "title": "",
                    "author": "Frank Herbert",
                    "year": 1965
                }
                """;

        mockMvc.perform(
                        post("/api/books")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any());
    }
}
