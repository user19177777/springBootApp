package com.example.demo.services;

import com.example.demo.models.Book;
import com.example.demo.models.Person;
import com.example.demo.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;
    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(boolean sortByYear){
        if (sortByYear)
            return booksRepository.findAll(Sort.by("Year"));
        else
        return booksRepository.findAll();
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPages, boolean sortByYear){
        if (sortByYear)
            return booksRepository.findAll(PageRequest.of(page,booksPerPages,Sort.by("Year"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page,booksPerPages)).getContent();
    }

    public Book findOne(int id){
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElse(null);
    }

    public List<Book> searchByTitle(String query){
        return booksRepository.findByTitleStartingWith(query);
    }
    @Transactional
    public void save(Book book){
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updBook){
        Book bookToBeUpd = booksRepository.findById(id).get();
        // добавляем по сути новую книгу (которая не находится в Persistence context), поэтому нужен save()
        updBook.setId(id);
        updBook.setOwner(bookToBeUpd.getOwner());// чтобы не терялась связь при обновлении

        booksRepository.save(updBook);
    }
    @Transactional
    public void delete(int id){
        booksRepository.deleteById(id);
    }
    // Returns null if book has no owner
    public Person getBookOwner(int id){
        // Здесь Hibernate.initialize() не нужен, так как владелец (сторона One) загружается не лениво
        return booksRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    // Освобождает книгу (этот метод вызывается, когда человек возвращает книгу в библиотеку)
    @Transactional
    public void release(int id) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                });
    }

    // Назначает книгу человеку (этот метод вызывается, когда человек забирает книгу из библиотеки)
    @Transactional
    public void assign(int id, Person selectedPerson) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date()); // текущее время
                }
        );
    }
}
