package rest;

import java.util.ArrayList;
import java.util.List;

public class BookService {
    private static final List<Book> BOOKS = new ArrayList<>();
    public static final BookService instance = new BookService();

    private BookService() {
    }

    public void add(Book book) {
        BOOKS.add(book);
    }

    public List<Book> find() {
        return BOOKS;
    }

    public void removeAll() {
        BOOKS.clear();
    }
}
