package library.dao;

import library.model.Person;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PersonDao implements Dao<Integer, Person> {

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

//    public List<Person> index() {
//        List<Person> people = new ArrayList<>();
//
//        try {
//            Statement statement = connection.createStatement();
//            String SQL = "SELECT * FROM Person";
//            ResultSet resultSet = statement.executeQuery(SQL);
//
//            while (resultSet.next()) {
//                Person person = new Person();
//
//                person.setId(resultSet.getInt("id"));
//                person.setName(resultSet.getString("name"));
//                person.setEmail(resultSet.getString("email"));
//                person.setAge(resultSet.getInt("age"));
//
//                people.add(person);
//            }
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//        return people;
//    }

    private static final String FIND_ALL_SQL = "SELECT id, name, year_of_birth FROM person";


    @Override
    public boolean update(Person person) {
        String SQL = "UPDATE person SET name = ?, year_of_birth = ? WHERE id = ?";
        try (var c = connection.prepareStatement(SQL)) {

            c.setString(1, person.getName());
            c.setInt(2, person.getYearOfBirth());
            c.setInt(3, person.getId());

            return c.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Person> findAll() {

        String SQL = "SELECT * FROM Person";

        try (var c = connection.prepareStatement(SQL)) {
            var resultSet = c.executeQuery();

            List<Person> persons = new ArrayList<>();

            while (resultSet.next()) {
                Person person = new Person(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("year_of_birth")
                );

                persons.add(person);
            }

            return persons;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Person save(Person person) {
        String SQL = "INSERT INTO person (name, year_of_birth) VALUES (?, ?)";
        try (var c = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            c.setObject(1, person.getName());
            c.setObject(2, person.getYearOfBirth());

            c.executeUpdate();

            var generatedKeys = c.getGeneratedKeys();
            generatedKeys.next();
            person.setId(generatedKeys.getObject("id", Integer.class));

            return person;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Person> findById(Integer id) {
        String SQL = "SELECT * FROM person WHERE id = ?";
        try (var c = connection.prepareStatement(SQL)) {
            c.setObject(1, id);

            var resultSet = c.executeQuery();
            Person person = null;
            if (resultSet.next()) {
                person = new Person(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("year_of_birth")
                );
            }

            return Optional.ofNullable(person);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String SQL = "DELETE FROM person WHERE id = ?";
        try (var c = connection.prepareStatement(SQL)) {
            c.setInt(1, id);


            return c.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
