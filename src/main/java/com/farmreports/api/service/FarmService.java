package com.farmreports.api.service;

import com.farmreports.api.dto.LivestockTypeDto;
import com.farmreports.api.dto.WorkerDto;
import com.farmreports.api.dto.WorkerRequest;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.entity.Worker;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.LivestockTypeRepository;
import com.farmreports.api.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final WorkerRepository workerRepository;
    private final LivestockTypeRepository livestockTypeRepository;
    private final FarmRepository farmRepository;

    @Transactional(readOnly = true)
    public List<WorkerDto> getActiveWorkers(Integer farmId) {
        return workerRepository.findByFarmIdAndActiveTrue(farmId)
                .stream()
                .map(w -> new WorkerDto(w.getId(), w.getName()))
                .toList();
    }

    @Transactional
    public WorkerDto addWorker(Integer farmId, WorkerRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
        Worker worker = new Worker();
        worker.setFarm(farm);
        worker.setName(request.name().trim());
        worker.setActive(true);
        Worker saved = workerRepository.save(worker);
        return new WorkerDto(saved.getId(), saved.getName());
    }

    @Transactional
    public void deactivateWorker(Integer farmId, Integer workerId) {
        Worker worker = workerRepository.findByIdAndFarmId(workerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));
        worker.setActive(false);
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
