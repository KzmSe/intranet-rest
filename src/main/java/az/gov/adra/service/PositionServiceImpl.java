package az.gov.adra.service;

import az.gov.adra.entity.Position;
import az.gov.adra.repository.interfaces.PositionRepository;
import az.gov.adra.service.interfaces.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

    private PositionRepository positionRepository;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public List<Position> findAllPositions() {
        return positionRepository.findAllPositions();
    }

}
