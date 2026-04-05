package org.example.lablinkbackend.domain.publication.model.dto;

import lombok.Data;

@Data
public class AuthorDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private Integer authorOrder;
    private Boolean isCorrespondingAuthor;
}