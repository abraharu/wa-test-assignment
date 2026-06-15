package org.watech.watestassignment.service;

import org.springframework.stereotype.Service;
import org.watech.watestassignment.client.SwapiClient;
import org.watech.watestassignment.dto.PeopleResponse;
import org.watech.watestassignment.dto.PersonDetailsResponse;
import org.watech.watestassignment.dto.PersonDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PeopleService {

    private static final int SIZE = 10;

    private final SwapiClient swapiClient;

    private final Map<Integer, PeopleResponse> pageCache = new ConcurrentHashMap<>();

    public PeopleService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    public PeopleResponse getPeople(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }
        return pageCache.computeIfAbsent(page, this::loadPeoplePage);
    }


    public PersonDetailsResponse getPersonById(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        PersonDto person = swapiClient.getPersonById(id);
        return new PersonDetailsResponse(person.getName(),
                                         parseHeight(person.getHeight()),
                                         parseMass(person.getMass()),
                                         person.getBirthYear(),
                                         person.getFilms() == null ? 0 : person.getFilms().size(),
                                         person.getCreated().substring(8, 10) + "-"
                                                 + person.getCreated().substring(5, 7) + "-"
                                                 + person.getCreated().substring(0, 4));
    }

    private PeopleResponse loadPeoplePage(int page) {
        List<PersonDto> allPeople = swapiClient.getPeople();
        int from = (page - 1) * SIZE;

        if (from >= allPeople.size()) {
            return new PeopleResponse(allPeople.size(), page, SIZE, List.of());
        }
        int to = Math.min(from + SIZE, allPeople.size());
        return new PeopleResponse(allPeople.size(), page, SIZE, allPeople.subList(from, to));
    }

    private Double parseHeight(String height) {
        if (height == null || "unknown".equalsIgnoreCase(height)) {
            return null;
        }
        return Double.parseDouble(height) / 100;
    }

    private Double parseMass(String mass) {
        if (mass == null || "unknown".equalsIgnoreCase(mass)) {
            return null;
        }
        return Double.parseDouble(mass.replace(",", ""));
    }
}