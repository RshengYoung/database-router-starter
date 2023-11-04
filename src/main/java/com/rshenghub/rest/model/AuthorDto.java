package com.rshenghub.rest.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_EMPTY)
public class AuthorDto {
    private String createUserId;
    private LocalDateTime createTime;
    private String modifyUserId;
    private LocalDateTime modifyTime;
}
