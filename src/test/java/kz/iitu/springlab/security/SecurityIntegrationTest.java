
package kz.iitu.springlab.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:lab10security;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // TEST 1: Anonymous user must be redirected to login
    @Test
    @WithAnonymousUser
    void anonymousUserRedirectedToLogin()
            throws Exception {

        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // TEST 2: USER must not access the admin panel
    @Test
    @WithMockUser(
            username = "user",
            roles = {"USER"}
    )
    void regularUserCannotAccessAdmin()
            throws Exception {

        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    // TEST 3: ADMIN must access the admin panel
    @Test
    @WithMockUser(
            username = "admin",
            roles = {"ADMIN", "USER"}
    )
    void adminCanAccessAdmin()
            throws Exception {

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }
}
