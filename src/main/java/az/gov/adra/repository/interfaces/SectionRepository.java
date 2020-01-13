package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Section;

import java.util.List;

public interface SectionRepository {

    List<Section> findAllSections();

    List<Section> findSectionsByDepartmentId(int id);

}
