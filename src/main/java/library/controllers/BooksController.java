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

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/index";
    }

    @GetMapping("/new")
    public String newBooks(@ModelAttribute("book") Book book) {
        System.out.println("Adding new book form");
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {

        if (bindingResult.hasErrors()){
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
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("book", bookService.findById(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()){
            return "books/edit";
        }

        bookService.update(id ,book);
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
