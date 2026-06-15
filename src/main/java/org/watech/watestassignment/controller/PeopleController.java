package org.watech.watestassignment.controller;

import org.springframework.web.bind.annotation.*;
import org.watech.watestassignment.dto.PeopleResponse;
import org.watech.watestassignment.dto.PersonDetailsResponse;
import org.watech.watestassignment.service.PeopleService;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping
    public PeopleResponse getPeople(@RequestParam(defaultValue = "1")
                                    int page) {
        return peopleService.getPeople(page);
    }

    @GetMapping("/{id}")
    public PersonDetailsResponse getPerson(@PathVariable int id) {
        return peopleService.getPersonById(id);
    }
}