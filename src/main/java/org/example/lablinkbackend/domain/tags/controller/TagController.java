package org.example.lablinkbackend.domain.tags.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.lablinkbackend.domain.organization.model.entity.Organization;
import org.example.lablinkbackend.domain.tags.model.entity.Tag;
import org.example.lablinkbackend.domain.tags.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(
            summary = "Получить все теги",
            description = "Возвращает список всех тегов"
    )
    @GetMapping("/all")
    public List<Tag> getAll() {
        return this.tagService.getAll();
    }
}
