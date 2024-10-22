package uk.co.autoventive.ymscachelambda.dao;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import uk.co.autoventive.ymscachelambda.exception.MissingEnviromentVariableException;
import uk.co.autoventive.ymscachelambda.model.GeoFence;
import uk.co.autoventive.ymscachelambda.service.JdbiService;
import java.util.List;

@Slf4j
public class GeoFenceDaoJdbiImpl extends GeoFenceDao {
    public GeoFenceDaoJdbiImpl() {}

    private final JdbiService jdbiService = new JdbiService();

    @Override
    public List<GeoFence> getGeoFences() {
        try {
            log.info("Creating JDBI connection");
            Jdbi jdbi = jdbiService.getConnection();
            log.info("Created JDBI Connection");
            return jdbi.withHandle(handle -> handle.createQuery("SELECT id, geojson FROM tbl__geofence").mapToBean(GeoFence.class).list());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
