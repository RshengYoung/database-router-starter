package com.rshenghub.rest.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class CompanyDto {
    private String companyId;
    private String companyName;
    private String companyCname;
    private String companyEname;
    private LocalDateTime since;
    private AuthorDto author;
    private List<CompanySiteDto> sites;
}
