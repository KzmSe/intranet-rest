package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Position;

import java.util.List;

public interface PositionRepository {

    List<Position> findAllPositions();

}
