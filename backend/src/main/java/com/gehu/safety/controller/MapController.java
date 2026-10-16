package com.gehu.safety.controller;

import com.gehu.safety.dto.response.ApiResponse;
import com.gehu.safety.dto.response.MapConfigResponse;
import com.gehu.safety.dto.response.SafeZoneResponse;
import com.gehu.safety.service.MapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public (see SecurityConfig: /api/map/** -> permitAll) since this is
 * non-sensitive reference data - the campus bounding box and safe-zone
 * markers are meant to be visible without logging in, same as any campus
 * map poster would be.
 */
@RestController
@RequestMapping("/api/map")
public class MapController {

    private final MapService mapService;


    public MapController(MapService mapService) {
        this.mapService = mapService;
    }
    @GetMapping("/config")
    public ApiResponse<MapConfigResponse> getMapConfig() {
        return ApiResponse.success(mapService.getMapConfig());
    }

    @GetMapping("/safe-zones")
    public ApiResponse<List<SafeZoneResponse>> getSafeZones() {
        return ApiResponse.success(mapService.getSafeZones());
    }
}
