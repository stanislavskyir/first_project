package library.controllers;

import jakarta.validation.Valid;
import library.model.Book;
import library.services.BookService;
import library.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    //private final BooksDao booksDao;
    private final BookService bookService;

    private final PeopleService peopleService;
    //private final PersonDao personDao;

    @Autowired
    public BooksController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

//    @GetMapping()
//    public String index(Model model) {
//        model.addAttribute("books", bookService.findAll());
//        return "books/index";
//    }

//    @GetMapping
//    public String index(Model model, @RequestParam(value = "page", required = false) Integer  page,
//                        @RequestParam(value = "books_per_page", required = false) Integer  booksPerPage) {
//
//        if (page != null && booksPerPage != null) {
//            model.addAttribute("books", bookService.findWithPagination(page, booksPerPage));
//        }else {
//            model.addAttribute("books", bookService.findAll());
//        }
//
//        return "books/index";
//    }

    @GetMapping
    public String index(Model model,
                        @RequestParam(value = "page", required = false) Integer  page,
                        @RequestParam(value = "books_per_page", required = false) Integer  booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) Boolean sortByYear
                        ) {
        if (page != null && booksPerPage != null && Boolean.TRUE.equals(sortByYear)) {
            model.addAttribute("books", bookService.findAllWithPaginationAndSort(page, booksPerPage));
        } else if (page != null && booksPerPage != null) {
            model.addAttribute("books", bookService.findWithPagination(page, booksPerPage));
        } else if (Boolean.TRUE.equals(sortByYear)) {
            model.addAttribute("books", bookService.findWithSortByYear());
        } else {
            model.addAttribute("books", bookService.findAll());
        }

        return "books/index";
    }

    @GetMapping("/new")
    public String newBooks(@ModelAttribute("book") Book book) {
        System.out.println("Adding new book form");
        return "books/new";
    }

    @GetMapping("/search")
    public String searchBooks() {
        return "books/search";
    }

    @PostMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        model.addAttribute("books", bookService.searchByNameOrAuthor(query));
        model.addAttribute("query", query);
        return "books/search";
    }



    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("people", peopleService.findAll());



        return "books/show";
    }


    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", bookService.findById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        bookService.update(id, book);
        return "redirect:/books";
    }


    @PostMapping("/{id}/assign")
    public String assignBook(@PathVariable("id") int bookId, @RequestParam("personId") int personId) {

        bookService.assignToPerson(bookId, personId);
        return "redirect:/books/" + bookId;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

}
