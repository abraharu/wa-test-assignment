package org.watech.watestassignment.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.watech.watestassignment.dto.PersonDto;

import java.util.List;

@Component
public class SwapiClient {

    private final RestClient restClient;

    public SwapiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<PersonDto> getPeople() {
        return restClient.get()
                .uri("/people")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public PersonDto getPersonById(int id) {
        return restClient.get()
                .uri("/people/" + id)
                .retrieve()
                .body(PersonDto.class);
    }
}
