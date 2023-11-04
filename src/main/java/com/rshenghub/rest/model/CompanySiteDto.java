package com.rshenghub.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class CompanySiteDto {
    private String siteId;
    private String siteName;
    private String siteCname;
    private String siteEname;
    private String address;
    private AuthorDto author;
}
