package com.gehu.safety.service;

import com.gehu.safety.dto.response.MapConfigResponse;
import com.gehu.safety.dto.response.SafeZoneResponse;

import java.util.List;

public interface MapService {
    MapConfigResponse getMapConfig();
    List<SafeZoneResponse> getSafeZones();
}
