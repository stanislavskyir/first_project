package library.services;

import library.model.Book;
import library.model.Person;
import library.repositories.BooksRepository;
import library.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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

    public List<Book> findAllWithPaginationAndSort(int page, int booksPerPage) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
    }

    public List<Book> findWithPagination(int page, int booksPerPage) {
        Pageable pageable = PageRequest.of(page, booksPerPage);
        return booksRepository.findAll(pageable).getContent();
    }

    public List<Book> findWithSortByYear() {
        Sort sort = Sort.by("year");
        return booksRepository.findAll(sort);
    }

//    public Page<Book> findAll(Pageable pageable) {
//        return booksRepository.findAll(pageable);
//    }

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
        Person person = peopleRepository.findById(personId).get();

        book.setCreatedAt(new Date()); // <--- ВОТ ЭТО ДОБАВЬ!
        book.setPerson(person);

        booksRepository.save(book);

    }

    public List<Book> searchByNameOrAuthor(String query) {
        return booksRepository.findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    public List<Book> getOverdueBooksByPersonId(int personId) {
        List<Book> personBooks = findByPersonId(personId);
        List<Book> overdueBooks = new ArrayList<>();

        long now = System.currentTimeMillis();
        long tenSecondsInMillis = 10 * 1000L;

        for (Book book : personBooks) {
            if (book.getCreatedAt() != null) {
                long diff = now - book.getCreatedAt().getTime();
                if (diff > tenSecondsInMillis) {
                    overdueBooks.add(book);
                }
            }
        }

        return overdueBooks;
    }

}
