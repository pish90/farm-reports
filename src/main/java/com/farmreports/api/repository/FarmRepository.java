package com.farmreports.api.repository;

import com.farmreports.api.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Integer> {}
