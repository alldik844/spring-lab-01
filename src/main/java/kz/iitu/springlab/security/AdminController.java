
package kz.iitu.springlab.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String adminPage(
            Model model,
            Authentication authentication
    ) {

        model.addAttribute(
                "username",
                authentication.getName()
        );

        model.addAttribute(
                "users",
                adminService.getAllUsers()
        );

        return "admin";
    }
}
