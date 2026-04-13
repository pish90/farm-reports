package com.farmreports.api.service;

import com.farmreports.api.dto.LivestockTypeDto;
import com.farmreports.api.dto.WorkerDto;
import com.farmreports.api.repository.LivestockTypeRepository;
import com.farmreports.api.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final WorkerRepository workerRepository;
    private final LivestockTypeRepository livestockTypeRepository;

    @Transactional(readOnly = true)
    public List<WorkerDto> getActiveWorkers(Integer farmId) {
        return workerRepository.findByFarmIdAndActiveTrue(farmId)
                .stream()
                .map(w -> new WorkerDto(w.getId(), w.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, List<LivestockTypeDto>> getLivestockTypesByCategory(Integer farmId) {
        return livestockTypeRepository.findByFarmId(farmId)
                .stream()
                .collect(Collectors.groupingBy(
                        lt -> lt.getCategory().name(),
                        Collectors.mapping(
                                lt -> new LivestockTypeDto(lt.getId(), lt.getCategory().name(), lt.getType()),
                                Collectors.toList()
                        )
                ));
    }
}
