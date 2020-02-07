package az.gov.adra.service;

import az.gov.adra.entity.Section;
import az.gov.adra.repository.interfaces.SectionRepository;
import az.gov.adra.service.interfaces.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    private SectionRepository sectionRepository;

    @Autowired
    public SectionServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public List<Section> findAllSections() {
        return sectionRepository.findAllSections();
    }

    @Override
    public List<Section> findSectionsByDepartmentId(int id) {
        return sectionRepository.findSectionsByDepartmentId(id);
    }

}
