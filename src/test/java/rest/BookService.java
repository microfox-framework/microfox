package rest;

import java.util.List;

public class BookService {

    public static void save(Book book) {
        BookRepository.instance.insert(book);
    }

    public static List<Book> find() {
        return BookRepository.instance.select();
    }

    public static Book find(int id) {
        return BookRepository.instance.select(id);
    }

    public static void remove(int id) {
        BookRepository.instance.delete(id);
    }
}
