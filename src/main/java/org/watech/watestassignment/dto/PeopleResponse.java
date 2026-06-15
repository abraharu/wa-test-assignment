package org.watech.watestassignment.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public record PeopleResponse(int total,int page, int pageSize, List<PersonDto> results) {
}