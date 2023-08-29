package com.hackclub.common.geo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.hackclub.clubs.models.GeoPoint;
import com.hackclub.clubs.models.GithubInfo;
import com.hackclub.common.file.Cache;

import java.io.IOException;
import java.util.Optional;

/**
 * Integrates with Google's geocoding APIs.  Primarily, we want to transform full address strings to lat/lng coords
 */
public class Geocoder {
    private static GeoApiContext geoApi = null;

    public static void initialize(String apiKey) {
        geoApi = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    /**
     * Convert an address string to a GeoPoint
     * @param address A full or partial address
     * @return An Optional GeoPoint
     * @throws IOException
     * @throws InterruptedException
     * @throws ApiException
     */
    public static Optional<GeoPoint> geocode(String address) throws IOException, InterruptedException, ApiException {
        String geocodingCacheKey = "geocoding_" + address;

        Optional<String> cachedGeo = Cache.load(geocodingCacheKey);
        if (cachedGeo.isPresent()) {
            return Optional.of(new ObjectMapper().readValue(cachedGeo.get(), GeoPoint.class));
        } else {
            GeocodingResult[] results = GeocodingApi.geocode(geoApi, address).await();
            if (results.length == 0)
                return Optional.empty();

            GeoPoint ret = new GeoPoint(results[0].geometry.location.lat, results[0].geometry.location.lng);
            System.out.println("Caching geopoint");
            Cache.save(geocodingCacheKey, new ObjectMapper().writeValueAsString(ret));

            return Optional.of(ret);
        }
    }

    public static void shutdown() {
        geoApi.shutdown();
    }
}
