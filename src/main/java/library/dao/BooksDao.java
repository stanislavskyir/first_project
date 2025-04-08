package library.dao;

import library.model.Book;
import library.model.Person;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BooksDao implements Dao<Integer, Book> {

    private static final String URL = "jdbc:postgresql://localhost:5432/first_project";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";
    private static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean update(Book book) {
        String SQL = "UPDATE book SET name = ?, author = ?, year = ? WHERE id = ?";
        try (var c = connection.prepareStatement(SQL)) {

            c.setString(1, book.getName());
            c.setString(2, book.getAuthor());
            c.setInt(3, book.getYear());
            c.setInt(4, book.getId());

            return c.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> findAll() {
        String SQL = "SELECT * FROM Book";

        try (var c = connection.prepareStatement(SQL)) {
            var resultSet = c.executeQuery();

            List<Book> books = new ArrayList<>();

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getInt("person_id"),
                        resultSet.getString("name"),
                        resultSet.getString("author"),
                        resultSet.getInt("year")
                );

                books.add(book);
            }

            return books;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> findByPersonId(int personId) {
        String SQL = "SELECT * FROM Book WHERE person_id = ?";
        try (var c = connection.prepareStatement(SQL)) {

            c.setInt(1, personId);
            var resultSet = c.executeQuery();
            List<Book> books = new ArrayList<>();

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getInt("person_id"),
                        resultSet.getString("name"),
                        resultSet.getString("author"),
                        resultSet.getInt("year")
                );

                books.add(book);
            }

            return books;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Book> findById(Integer id) {
        String SQL = "SELECT * FROM book WHERE id = ?";
        try (var c = connection.prepareStatement(SQL)) {
            c.setObject(1, id);

            var resultSet = c.executeQuery();
            Book book = null;
            if (resultSet.next()) {
                book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getInt("person_id"),
                        resultSet.getString("name"),
                        resultSet.getString("author"),
                        resultSet.getInt("year")
                );
            }

            return Optional.ofNullable(book);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Book save(Book book) {
        String SQL = "INSERT INTO book (name, author, year) VALUES (?, ?, ?)";
        try (var c = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            c.setObject(1, book.getName());
            c.setObject(2, book.getAuthor());
            c.setObject(3, book.getYear());

            c.executeUpdate();

            var generatedKeys = c.getGeneratedKeys();
            generatedKeys.next();
            book.setId(generatedKeys.getObject("id", Integer.class));

            return book;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String SQL = "DELETE FROM book WHERE id = ?";
        try (var c = connection.prepareStatement(SQL)) {
            c.setInt(1, id);


            return c.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void assignToPerson(int bookId, int personId) {
        String SQL = "UPDATE book SET person_id = ? WHERE id = ?";
        try (var c = connection.prepareStatement(SQL)) {

            c.setInt(1, personId);
            c.setInt(2, bookId);

            c.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
