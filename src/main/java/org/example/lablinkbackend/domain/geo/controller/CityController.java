package org.example.lablinkbackend.domain.geo.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.lablinkbackend.domain.geo.model.entity.City;
import org.example.lablinkbackend.domain.geo.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/city")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @Operation(
            summary = "Получить все города для выбора нужного",
            description = "Возвращает список всех городов"
    )
    @GetMapping("/all")
    public List<City> getAllCities() {
        return this.cityService.getAll();
    }
}
