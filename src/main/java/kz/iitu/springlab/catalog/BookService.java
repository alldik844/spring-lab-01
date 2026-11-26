
package kz.iitu.springlab.catalog;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    // USER and ADMIN can view books
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Book> findAll(String author) {

        if (author == null || author.isBlank()) {
            return repository.findAll();
        }

        return repository.findAll()
                .stream()
                .filter(book -> book.author()
                        .toLowerCase()
                        .contains(author.toLowerCase()))
                .toList();
    }

    // USER and ADMIN can view a single book
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    // Only ADMIN can create books
    @PreAuthorize("hasRole('ADMIN')")
    public Book create(Book book) {

        return repository.save(
                new Book(
                        null,
                        book.title(),
                        book.author(),
                        book.year()
                )
        );
    }

    // Only ADMIN can update books
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Book> update(long id, Book book) {

        if (repository.findById(id).isEmpty()) {
            return Optional.empty();
        }

        Book updated = new Book(
                id,
                book.title(),
                book.author(),
                book.year()
        );

        return Optional.of(repository.save(updated));
    }

    // Only ADMIN can delete books
    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}
