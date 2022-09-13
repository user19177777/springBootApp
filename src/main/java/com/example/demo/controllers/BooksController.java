package com.example.demo.controllers;

import com.example.demo.models.Book;
import com.example.demo.models.Person;
import com.example.demo.services.BooksService;
import com.example.demo.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;
    private final PeopleService peopleService;
    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;

        this.peopleService = peopleService;
    }

    @GetMapping
    public String index(Model model, @RequestParam(value = "page", required = false)Integer page,
                        @RequestParam(value = "books_per_page",required = false)Integer booksPerPage,
                        @RequestParam(value = "sort_by_year",required = false)boolean sortByYear){
        if (page==null || booksPerPage==null)
            model.addAttribute("books",booksService.findAll(sortByYear)); // выдача всех книг
        else
            model.addAttribute("books",booksService.findWithPagination(page,booksPerPage,sortByYear));
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id")int id, Model model, @ModelAttribute("person")Person person){
        model.addAttribute("book",booksService.findOne(id));
        Person bookOwner = booksService.getBookOwner(id);
        if (bookOwner != null)
            model.addAttribute("owner",bookOwner);
        else
            model.addAttribute("people",peopleService.findAll());
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book){
        return "books/new";
    }

    @PostMapping
    public String create(@ModelAttribute("book")@Valid Book book,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "books/new";
        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model,@PathVariable("id")int id){
        model.addAttribute("book",booksService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book")@Valid Book book,
                         BindingResult bindingResult,
                         @PathVariable("id")int id){
        if (bindingResult.hasErrors())
            return "books/edit";
        booksService.update(id,book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id")int id){
        booksService.delete(id);
        return "redirect:/books";
    }

    //освободить книгу
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id")int id){
        booksService.release(id);
        //redirect на страницу книги с id
        return "redirect:/books/"+id;
    }

    //назначить книгу выбранному человеку
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id")int id,@ModelAttribute("person")Person selectedPerson){
        //назначено только id у selectedPerson, остальные поля - null
        booksService.assign(id,selectedPerson);
        return "redirect:/books/"+id;
    }

    @GetMapping("/search")
    public String searchPage(){
        return "books/search";
    }
    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query")String query){
        model.addAttribute("books",booksService.searchByTitle(query));
        return "books/search";
    }
}
