package library.services;

import library.model.Book;
import library.model.Person;
import library.repositories.BooksRepository;
import library.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BooksRepository booksRepository;
    private final PeopleRepository peopleRepository;

    @Autowired
    public BookService(BooksRepository booksRepository, PeopleRepository peopleRepository) {
        this.booksRepository = booksRepository;
        this.peopleRepository = peopleRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public Book findById(int id) {
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    public List<Book> findByPersonId(int personId) {
        return booksRepository.findByPersonId(personId);
    }

    @Transactional
    public void assignToPerson(int bookId, int personId) {
        Book book = findById(bookId);


        //Person person = peopleRepository.findById(personId).get();
        Person person = peopleRepository.findById(personId).get();
        System.out.println("Person person = peopleRepository.findById(personId).get();");

        book.setPerson(person);

        booksRepository.save(book);
        System.out.println("booksRepository.save(book);");
    }

}
