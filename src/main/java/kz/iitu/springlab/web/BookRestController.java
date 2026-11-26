
package kz.iitu.springlab.web;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(
                    value = "Accept-Language",
                    required = false,
                    defaultValue = "en"
            ) String language) {

        return service.findAll(author)
                .stream()
                .limit(limit)
                .map(book -> localizeBook(book, language))
                .toList();
    }

    private Map<String, Object> localizeBook(
            Book book,
            String language) {

        Map<String, Object> result = new LinkedHashMap<>();

        String lang = language.toLowerCase();

        if (lang.startsWith("ru")) {
            result.put("ид", book.id());
            result.put("название", book.title());
            result.put("автор", book.author());
            result.put("год", book.year());
            return result;
        }

        if (lang.startsWith("kk") || lang.startsWith("kz")) {
            result.put("ид", book.id());
            result.put("атауы", book.title());
            result.put("автор", book.author());
            result.put("жыл", book.year());
            return result;
        }

        result.put("id", book.id());
        result.put("title", book.title());
        result.put("author", book.author());
        result.put("year", book.year());

        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> find(@PathVariable long id) {

        Optional<Book> book = service.findById(id);

        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                "Book not found: " + id
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problem);
    }

    public record CreateBookRequest(
            @NotBlank String title,
            @NotBlank String author,
            @NotNull @Min(1) Integer year
    ) {}

    @PostMapping
    public ResponseEntity<Book> create(
            @Valid @RequestBody CreateBookRequest request) {

        Book book = new Book(
                null,
                request.title(),
                request.author(),
                request.year()
        );

        Book saved = service.create(book);

        return ResponseEntity
                .created(URI.create("/api/books/" + saved.id()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(
            @PathVariable long id,
            @RequestBody Book book) {

        return service.update(id, book)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable long id) {

        if (service.delete(id)) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity
                .notFound()
                .build();
    }
}
