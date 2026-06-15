package org.watech.watestassignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PersonDetailsResponse(String name,
                                    Double height,
                                    Double mass,
                                    @JsonProperty("birth_year")
                                    String birthYear,
                                    @JsonProperty("number_of_films")
                                    Integer numberOfFilms,
                                    @JsonProperty("date_added")
                                    String dateAdded) {

}