package org.watech.watestassignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PersonDto {

    private String name;

    private String height;

    private String mass;

    @JsonProperty("birth_year")
    private String birthYear;

    private List<String> films;

    private String created;

}
