package kz.iitu.springlab.web;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    // VARIANT 8:
    // Field names depend on Accept-Language
    //
    // GET /api/books
    // GET /api/books?author=Bloch&limit=1
    @GetMapping
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(
                    value = "Accept-Language",
                    required = false,
                    defaultValue = "en"
            ) String language
    ) {

        return service.findAll(author)
                .stream()
                .limit(limit)
                .map(book -> localizeBook(book, language))
                .toList();
    }

    private Map<String, Object> localizeBook(
            Book book,
            String language
    ) {

        Map<String, Object> result = new LinkedHashMap<>();

        String lang = language.toLowerCase();

        // Russian
        if (lang.startsWith("ru")) {
            result.put("ид", book.id());
            result.put("название", book.title());
            result.put("автор", book.author());
            result.put("год", book.year());

            return result;
        }

        // Kazakh
        if (lang.startsWith("kk") || lang.startsWith("kz")) {
            result.put("ид", book.id());
            result.put("атауы", book.title());
            result.put("автор", book.author());
            result.put("жыл", book.year());

            return result;
        }

        // English by default
        result.put("id", book.id());
        result.put("title", book.title());
        result.put("author", book.author());
        result.put("year", book.year());

        return result;
    }

    // GET /api/books/1
    @GetMapping("/{id}")
    public ResponseEntity<Book> find(
            @PathVariable long id
    ) {

        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // POST /api/books
    @PostMapping
    public ResponseEntity<Book> create(
            @RequestBody Book book
    ) {

        Book saved = service.create(book);

        return ResponseEntity
                .created(
                        URI.create("/api/books/" + saved.id())
                )
                .body(saved);
    }

    // PUT /api/books/1
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(
            @PathVariable long id,
            @RequestBody Book book
    ) {

        return service.update(id, book)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // DELETE /api/books/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable long id
    ) {

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