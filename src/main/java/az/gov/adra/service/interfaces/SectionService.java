package az.gov.adra.service.interfaces;

import az.gov.adra.entity.Section;

import java.util.List;

public interface SectionService {

    List<Section> findAllSections();

    List<Section> findSectionsByDepartmentId(int id);

}
