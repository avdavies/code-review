package uk.co.autoventive.ymscachelambda.dao;

import uk.co.autoventive.ymscachelambda.model.GeoFence;

import java.util.Collections;
import java.util.List;

abstract public class GeoFenceDao {
    public List<GeoFence> getGeoFences() {
        return Collections.emptyList();
    };
}
