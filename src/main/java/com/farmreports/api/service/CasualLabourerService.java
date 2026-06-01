package com.farmreports.api.service;

import com.farmreports.api.dto.CasualLabourerDto;
import com.farmreports.api.dto.CasualLabourerRequest;
import com.farmreports.api.entity.CasualLabourer;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.repository.CasualLabourerRepository;
import com.farmreports.api.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CasualLabourerService {

    private final CasualLabourerRepository casualLabourerRepository;
    private final FarmRepository farmRepository;

    @Transactional(readOnly = true)
    public List<CasualLabourerDto> getActiveCasualLabourers(Integer farmId) {
        return casualLabourerRepository.findByFarmIdAndActiveTrue(farmId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CasualLabourerDto addCasualLabourer(Integer farmId, CasualLabourerRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        CasualLabourer labourer = new CasualLabourer();
        labourer.setFarm(farm);
        labourer.setName(request.name().trim());
        labourer.setPhone(request.phone() != null ? request.phone().trim() : null);
        labourer.setDefaultDailyRate(request.defaultDailyRate());

        if (request.photoBase64() != null && !request.photoBase64().isBlank()) {
            labourer.setPhotoData(Base64.getDecoder().decode(request.photoBase64()));
            labourer.setPhotoMimeType(request.photoMimeType() != null ? request.photoMimeType() : "image/jpeg");
        }

        return toDto(casualLabourerRepository.save(labourer));
    }

    @Transactional
    public void deactivateCasualLabourer(Integer farmId, Integer labourerId) {
        CasualLabourer labourer = casualLabourerRepository.findByIdAndFarmId(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));
        labourer.setActive(false);
    }

    private CasualLabourerDto toDto(CasualLabourer labourer) {
        String photoBase64 = null;
        if (labourer.getPhotoData() != null) {
            photoBase64 = Base64.getEncoder().encodeToString(labourer.getPhotoData());
        }
        return new CasualLabourerDto(
                labourer.getId(),
                labourer.getName(),
                labourer.getPhone(),
                labourer.getDefaultDailyRate(),
                photoBase64,
                labourer.getPhotoMimeType()
        );
    }
}
