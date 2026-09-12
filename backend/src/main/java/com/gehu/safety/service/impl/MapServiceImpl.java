package com.gehu.safety.service.impl;

import com.gehu.safety.config.MapScopeProperties;
import com.gehu.safety.dto.response.MapConfigResponse;
import com.gehu.safety.dto.response.SafeZoneResponse;
import com.gehu.safety.repository.SafeZoneRepository;
import com.gehu.safety.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final MapScopeProperties mapScopeProperties;
    private final SafeZoneRepository safeZoneRepository;

    @Override
    public MapConfigResponse getMapConfig() {
        return MapConfigResponse.builder()
                .centerLat(mapScopeProperties.getCenterLat())
                .centerLng(mapScopeProperties.getCenterLng())
                .boundsRadiusKm(mapScopeProperties.getBoundsRadiusKm())
                .build();
    }

    @Override
    public List<SafeZoneResponse> getSafeZones() {
        return safeZoneRepository.findAll().stream()
                .map(z -> SafeZoneResponse.builder()
                        .id(z.getId())
                        .name(z.getName())
                        .type(z.getType())
                        .lat(z.getLat())
                        .lng(z.getLng())
                        .description(z.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
