package uk.co.autoventive.ymscachelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import uk.co.autoventive.ymscachelambda.dao.GeoFenceDao;
import uk.co.autoventive.ymscachelambda.dao.GeoFenceDaoJdbiImpl;
import uk.co.autoventive.ymscachelambda.exception.MissingEnviromentVariableException;
import uk.co.autoventive.ymscachelambda.model.GeoFence;
import uk.co.autoventive.ymscachelambda.service.CacheUpdateService;
import uk.co.autoventive.ymscachelambda.service.YmsDataService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


@Slf4j
public class App implements RequestStreamHandler {
    CacheUpdateService cacheService;
    GeoFenceDao geoFenceDao;

    public App() {
        geoFenceDao = new GeoFenceDaoJdbiImpl();
        cacheService = new CacheUpdateService();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        // Get GeoFences from YMS
        try {
            log.info("Collecting Geofences");
            List<GeoFence> geoFences = geoFenceDao.getGeoFences();

            cacheService.updateCache(geoFences);
            log.info("Cache updated");
        } catch (MissingEnviromentVariableException e) {
            throw new RuntimeException(e);
        }
    }
}
