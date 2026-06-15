package org.watech.watestassignment.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.watech.watestassignment.dto.PeopleResponse;
import org.watech.watestassignment.dto.PersonDetailsResponse;
import org.watech.watestassignment.dto.PersonDto;
import org.watech.watestassignment.exception.GlobalExceptionHandler;
import org.watech.watestassignment.service.PeopleService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PeopleControllerTest {

    @Mock
    private PeopleService peopleService;

    @InjectMocks
    private PeopleController peopleController;

    @Test
    void getPeopleDelegatesPageToPeopleService() {
        PeopleResponse expected = new PeopleResponse(1, 2, 10, List.of(new PersonDto()));
        when(peopleService.getPeople(2)).thenReturn(expected);

        PeopleResponse response = peopleController.getPeople(2);

        assertSame(expected, response);
        verify(peopleService).getPeople(2);
    }

    @Test
    void getPersonDelegatesIdToPeopleService() {
        PersonDetailsResponse expected = new PersonDetailsResponse(
                "Luke Skywalker",
                1.72,
                77.0,
                "19BBY",
                5,
                "09-12-2014"
        );
        when(peopleService.getPersonById(1)).thenReturn(expected);

        PersonDetailsResponse response = peopleController.getPerson(1);

        assertSame(expected, response);
        verify(peopleService).getPersonById(1);
    }

    @Test
    void getPeoplePropagatesInvalidPageException() {
        when(peopleService.getPeople(0))
                .thenThrow(new IllegalArgumentException("Page must be greater than 0"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> peopleController.getPeople(0)
        );

        assertEquals("Page must be greater than 0", exception.getMessage());
        verify(peopleService).getPeople(0);
    }

    @Test
    void getPersonPropagatesInvalidIdException() {
        when(peopleService.getPersonById(0))
                .thenThrow(new IllegalArgumentException("Id must be greater than 0"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> peopleController.getPerson(0)
        );

        assertEquals("Id must be greater than 0", exception.getMessage());
        verify(peopleService).getPersonById(0);
    }

    @Test
    void getPeopleReturnsBadRequestForInvalidPage() throws Exception {
        when(peopleService.getPeople(0))
                .thenThrow(new IllegalArgumentException("Page must be greater than 0"));

        mockMvc().perform(get("/people").param("page", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page must be greater than 0"));
    }

    @Test
    void getPersonReturnsBadRequestForInvalidId() throws Exception {
        when(peopleService.getPersonById(0))
                .thenThrow(new IllegalArgumentException("Id must be greater than 0"));

        mockMvc().perform(get("/people/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id must be greater than 0"));
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(peopleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
}
