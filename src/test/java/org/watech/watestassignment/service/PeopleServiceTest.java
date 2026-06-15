package org.watech.watestassignment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.watech.watestassignment.client.SwapiClient;
import org.watech.watestassignment.dto.PeopleResponse;
import org.watech.watestassignment.dto.PersonDetailsResponse;
import org.watech.watestassignment.dto.PersonDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeopleServiceTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private PeopleService peopleService;

    @Test
    void getPeopleReturnsFirstPageWithPageSizeTen() {
        List<PersonDto> people = people(12);
        when(swapiClient.getPeople()).thenReturn(people);

        PeopleResponse response = peopleService.getPeople(1);

        assertAll(
                () -> assertEquals(12, response.total()),
                () -> assertEquals(1, response.page()),
                () -> assertEquals(10, response.pageSize()),
                () -> assertEquals(10, response.results().size()),
                () -> assertEquals("Person 1", response.results().get(0).getName()),
                () -> assertEquals("Person 10", response.results().get(9).getName())
        );
    }

    @Test
    void getPeopleReturnsPartialPageWhenLastPageHasLessThanPageSize() {
        List<PersonDto> people = people(12);
        when(swapiClient.getPeople()).thenReturn(people);

        PeopleResponse response = peopleService.getPeople(2);

        assertAll(
                () -> assertEquals(12, response.total()),
                () -> assertEquals(2, response.page()),
                () -> assertEquals(10, response.pageSize()),
                () -> assertEquals(2, response.results().size()),
                () -> assertEquals("Person 11", response.results().get(0).getName()),
                () -> assertEquals("Person 12", response.results().get(1).getName())
        );
    }

    @Test
    void getPeopleReturnsEmptyResultsWhenPageIsOutOfRange() {
        when(swapiClient.getPeople()).thenReturn(people(3));

        PeopleResponse response = peopleService.getPeople(2);

        assertAll(
                () -> assertEquals(3, response.total()),
                () -> assertEquals(2, response.page()),
                () -> assertEquals(10, response.pageSize()),
                () -> assertTrue(response.results().isEmpty())
        );
    }

    @Test
    void getPeopleCachesPages() {
        when(swapiClient.getPeople()).thenReturn(people(1));

        PeopleResponse first = peopleService.getPeople(1);
        PeopleResponse second = peopleService.getPeople(1);

        assertSame(first, second);
        verify(swapiClient, times(1)).getPeople();
    }

    @Test
    void getPeopleRejectsInvalidPage() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> peopleService.getPeople(0)
        );

        assertEquals("Page must be greater than 0", exception.getMessage());
        verifyNoInteractions(swapiClient);
    }

    @Test
    void getPersonByIdMapsSwapiPersonToDetailsResponse() {
        PersonDto person = person("Luke Skywalker");
        person.setHeight("172");
        person.setMass("1,358");
        person.setBirthYear("19BBY");
        person.setFilms(List.of("film-1", "film-2"));
        person.setCreated("2014-12-09T13:50:51.644000Z");
        when(swapiClient.getPersonById(1)).thenReturn(person);

        PersonDetailsResponse response = peopleService.getPersonById(1);

        assertAll(
                () -> assertEquals("Luke Skywalker", response.name()),
                () -> assertEquals(1.72, response.height()),
                () -> assertEquals(1358.0, response.mass()),
                () -> assertEquals("19BBY", response.birthYear()),
                () -> assertEquals(2, response.numberOfFilms()),
                () -> assertEquals("09-12-2014", response.dateAdded())
        );
    }

    @Test
    void getPersonByIdHandlesUnknownMetricsAndNullFilms() {
        PersonDto person = person("Unknown Person");
        person.setHeight("unknown");
        person.setMass(null);
        person.setBirthYear("unknown");
        person.setFilms(null);
        person.setCreated("2020-01-02T03:04:05.000000Z");
        when(swapiClient.getPersonById(5)).thenReturn(person);

        PersonDetailsResponse response = peopleService.getPersonById(5);

        assertAll(
                () -> assertEquals("Unknown Person", response.name()),
                () -> assertNull(response.height()),
                () -> assertNull(response.mass()),
                () -> assertEquals("unknown", response.birthYear()),
                () -> assertEquals(0, response.numberOfFilms()),
                () -> assertEquals("02-01-2020", response.dateAdded())
        );
    }

    @Test
    void getPersonByIdRejectsInvalidId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> peopleService.getPersonById(0)
        );

        assertEquals("Id must be greater than 0", exception.getMessage());
        verifyNoInteractions(swapiClient);
    }

    private static List<PersonDto> people(int count) {
        List<PersonDto> people = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            people.add(person("Person " + i));
        }
        return people;
    }

    private static PersonDto person(String name) {
        PersonDto person = new PersonDto();
        person.setName(name);
        person.setHeight("180");
        person.setMass("80");
        person.setBirthYear("unknown");
        person.setFilms(List.of());
        person.setCreated("2014-12-09T13:50:51.644000Z");
        return person;
    }
}