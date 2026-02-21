package org.example.lablinkbackend.domain.geo.repository;

import org.example.lablinkbackend.domain.geo.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

}
