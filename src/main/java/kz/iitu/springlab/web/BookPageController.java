
package kz.iitu.springlab.web;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService service;

    public BookPageController(BookService service) {
        this.service = service;
    }

    // Show all books
    @GetMapping
    public String list(
            @RequestParam(required = false) String author,
            Model model
    ) {

        model.addAttribute(
                "books",
                service.findAll(author)
        );

        model.addAttribute("author", author);
        model.addAttribute("title", "Catalogue");

        return "books/list";
    }

    // Create a book - ADMIN only
    @PostMapping
    public String create(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int year,
            RedirectAttributes redirect
    ) {

        Book saved = service.create(
                new Book(
                        null,
                        title,
                        author,
                        year
                )
        );

        redirect.addFlashAttribute(
                "message",
                "Book «" + saved.title()
                        + "» has been added"
        );

        return "redirect:/books";
    }

    // Delete a book - ADMIN only
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable long id,
            RedirectAttributes redirect
    ) {

        boolean deleted = service.delete(id);

        if (deleted) {
            redirect.addFlashAttribute(
                    "message",
                    "Book deleted successfully"
            );
        } else {
            redirect.addFlashAttribute(
                    "message",
                    "Book not found"
            );
        }

        return "redirect:/books";
    }
}
