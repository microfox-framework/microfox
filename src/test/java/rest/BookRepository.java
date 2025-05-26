package rest;

import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private static final List<Book> BOOKS = new ArrayList<>();
    public static final BookRepository instance = new BookRepository();

    private BookRepository() {
    }

    public void insert(Book book) {
        int id = BOOKS.isEmpty() ? 1 : BOOKS.getLast().getId() + 1;
        book.setId(id);
        BOOKS.add(book);
    }

    public List<Book> select() {
        return BOOKS;
    }

    public Book select(int id) {
        return BOOKS.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    public void delete(int id) {
        Book book = select(id);
        if (book != null) BOOKS.remove(book);
    }

    public void removeAll() {
        BOOKS.clear();
    }
}
