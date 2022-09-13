package com.example.demo.controllers;


import com.example.demo.models.Person;
import com.example.demo.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    @Autowired
    public PeopleController( PeopleService peopleService) {
        this.peopleService = peopleService;
    }
    //URL такой же /people REST URL
    @GetMapping
    public String index(Model model){
        //получим всех людей из ДАО и передадим в представление
        model.addAttribute("people",peopleService.findAll());
        return "people/index";
    }
    //REST URL
    @GetMapping("/{id}")
    public String show(
            //извлекает id из URL и помещает в аргументы метода, чтобы был доступ к id внутри метода
            @PathVariable("id")int id,
                       Model model){
        //получим одного человека по id из ДАО и передадим в представление
        model.addAttribute("person",peopleService.findOne(id));
        model.addAttribute("books",peopleService.getBookByPersonId(id));
        return "people/show";
    }
    @GetMapping("/new")
    public String newPerson(
            //создает новый объект Person по пустому конструктору и добавляет его в модель
            @ModelAttribute("person") Person person){
        return "people/new";
    }
    @PostMapping
    public String create(
            //создает новый объект Person (данные полей берет из URL) и добавляет его в модель
            //@Valid проверяет поля, которые приходят из формы по модели
            @ModelAttribute("person")@Valid Person person, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "people/new";
        peopleService.save(person);
        return "redirect:/people";
    }
    //показывает форму редактирования
    @GetMapping("/{id}/edit")
    public String edit(Model model,@PathVariable("id")int id){
        model.addAttribute("person",peopleService.findOne(id));
        return "people/edit";
    }
    //отправляем отредактированные данные
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person")@Valid Person person,BindingResult bindingResult,@PathVariable("id")int id){
        if (bindingResult.hasErrors())
            return "people/edit";
        peopleService.update(id,person);
        return "redirect:/people";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        peopleService.delete(id);
        return "redirect:/people";
    }
}
