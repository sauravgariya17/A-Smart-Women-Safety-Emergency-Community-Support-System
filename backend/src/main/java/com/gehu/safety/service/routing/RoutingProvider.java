package com.gehu.safety.service.routing;

/**
 * Swap-in point for real turn-by-turn routing.
 *
 * To upgrade later: implement this interface against OSRM (self-hosted,
 * free) or OpenRouteService (hosted, free tier with an API key), annotate
 * it @Primary or remove StraightLineRoutingProvider, and nothing else in
 * the codebase changes - JourneyService only depends on this interface.
 */
public interface RoutingProvider {
    RouteResult getRoute(double startLat, double startLng, double destLat, double destLng);
}
