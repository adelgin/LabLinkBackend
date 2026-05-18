package org.example.lablinkbackend.domain.feed.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostRequestDto {
    private String content;
    private Long groupId;
    private Long parentPostId;
    private List<String> fileUrls;
}
