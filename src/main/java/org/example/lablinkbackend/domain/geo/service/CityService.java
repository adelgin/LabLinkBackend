package org.example.lablinkbackend.domain.geo.service;

import org.example.lablinkbackend.domain.geo.model.entity.City;
import org.example.lablinkbackend.domain.geo.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getAll() {
        return this.cityRepository.findAll();
    }
}
