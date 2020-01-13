package az.gov.adra.service;

import az.gov.adra.entity.Idea;
import az.gov.adra.exception.IdeaCredentialsException;
import az.gov.adra.repository.interfaces.IdeaRepository;
import az.gov.adra.service.interfaces.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdeaServiceImpl implements IdeaService {

    private IdeaRepository ideaRepository;

    @Autowired
    public IdeaServiceImpl(IdeaRepository ideaRepository) {
        this.ideaRepository = ideaRepository;
    }

    @Override
    public void addIdea(Idea idea) throws IdeaCredentialsException {
        ideaRepository.addIdea(idea);
    }

}
