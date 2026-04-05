package org.example.lablinkbackend.domain.publication.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class PublicationRequestDto {
    private String doi;
    private String journalTitle;
    private String source;
    private String title;
    private String description;
    private String url;
    private Integer year;

    private List<CoAuthorDto> coAuthors;

    @Data
    public static class CoAuthorDto {
        private Long userId;
        private Integer authorOrder;
        private Boolean isCorrespondingAuthor;
    }
}