package uk.co.autoventive.ymscachelambda.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import uk.co.autoventive.ymscachelambda.exception.MissingEnviromentVariableException;
import uk.co.autoventive.ymscachelambda.model.GeoFence;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CacheUpdateService {
    private final String[] REQUIRED_VARIABLES = {"REDIS_HOST", "REDIS_PORT"};

    public void updateCache(List<GeoFence> geoFences) throws MissingEnviromentVariableException {

        checkForEnvironmentVariablesExist();
        log.info("Updating Cache");
        saveGeoFencesToCache(geoFences);
        log.info("Removing old GeoFences");
        removeMissingGeoFencesFromCache(geoFences);
    }

    private Jedis getRedisClient() {
        String host = System.getenv("REDIS_HOST");
        String portAsString = System.getenv("REDIS_PORT");
        int port = Integer.parseInt(portAsString);

        try (Jedis jedis = new Jedis(host, port, true)) {
            return jedis;
        }
    }

    private void removeMissingGeoFencesFromCache(List<GeoFence> geoFences) {

        // Convert ids to string for key comparison
        List<String> geoFenceStringIds = geoFences.stream().map(geofence -> String.valueOf(geofence.getId())).toList();
        ArrayList<String> idsMissing = new ArrayList<>();

        try (Jedis jedis = getRedisClient()) {
            String cursor = ScanParams.SCAN_POINTER_START;
            // Find all keys in batches of 100
            ScanParams scanParams = new ScanParams().match("*").count(100);

            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();
                for (String key : scanResult.getResult()) {
                    if (!geoFenceStringIds.contains(key)) {
                        idsMissing.add(key);
                    }
                }
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

            removeKeysFromCache(idsMissing);
        }
    }

    private void removeKeysFromCache(List<String> keys) {
        try (Jedis jedis = getRedisClient()) {
            for (String missingKey : keys) {
                jedis.del(missingKey);
                log.info("Deleted {}", missingKey);
            }
        }
    }

    private void saveGeoFencesToCache(List<GeoFence> geoFences) {
        String host = System.getenv("REDIS_HOST");
        String portAsString = System.getenv("REDIS_PORT");
        int port = Integer.parseInt(portAsString);

        try (Jedis jedis = new Jedis(host, port, true)) {
            for (GeoFence geoFence : geoFences) {
                String key = String.valueOf(geoFence.getId());
                if (jedis.exists(key)) {
                    log.info("Found existing key {}", key);
                } else {
                    log.info("Creating new entry for {}", key);
                }
                jedis.set(key, geoFence.getGeoJson());
            }
        }
    }

    private void checkForEnvironmentVariablesExist() throws MissingEnviromentVariableException {
        for (String variable : REQUIRED_VARIABLES) {
            if (System.getenv(variable) == null) {
                throw new MissingEnviromentVariableException("Missing " + variable);
            }
        }
    }
}
