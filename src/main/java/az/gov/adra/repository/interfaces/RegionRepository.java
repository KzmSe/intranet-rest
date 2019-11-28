package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Region;

import java.util.List;

public interface RegionRepository {

    List<Region> findAllRegions();

}
