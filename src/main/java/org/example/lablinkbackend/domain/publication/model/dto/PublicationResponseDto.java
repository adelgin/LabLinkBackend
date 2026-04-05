package org.example.lablinkbackend.domain.publication.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class PublicationResponseDto {
    private Long id;
    private String doi;
    private String journalTitle;
    private String source;
    private String title;
    private String description;
    private String url;
    private Integer year;
    private List<AuthorDto> authors;
}
