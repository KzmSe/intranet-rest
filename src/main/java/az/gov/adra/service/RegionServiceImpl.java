package az.gov.adra.service;

import az.gov.adra.entity.Region;
import az.gov.adra.repository.interfaces.RegionRepository;
import az.gov.adra.service.interfaces.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    private RegionRepository regionRepository;

    @Autowired
    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public List<Region> findAllRegions() {
        return regionRepository.findAllRegions();
    }
}
