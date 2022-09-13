package com.example.demo.services;

import com.example.demo.models.Book;
import com.example.demo.models.Person;
import com.example.demo.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll(){
        return peopleRepository.findAll();
    }

    public Person findOne(int id){
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }
    @Transactional
    public void save(Person person){
        peopleRepository.save(person);
    }
    @Transactional
    public void update(int id,Person updPerson){
        updPerson.setId(id);
        peopleRepository.save(updPerson);
    }
    @Transactional
    public void delete(int id){
        peopleRepository.deleteById(id);
    }

    public Optional<Person> getPersonByFullName(String fullName){
        return peopleRepository.findByName(fullName);
    }

    public List<Book> getBookByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            // явно подгружаем книги из бд(переводим в Persistence context )
            Hibernate.initialize(person.get().getBooks());

            //Проверка просроченности книг
            person.get().getBooks().forEach(book -> {
                long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                if (diffInMillies > 864000000)
                    book.setExpired(true);//книга просрочена, 864000000 - 10 суток в сек
            });
            return person.get().getBooks();
        }
        else {
            return Collections.emptyList();
        }
    }
}
