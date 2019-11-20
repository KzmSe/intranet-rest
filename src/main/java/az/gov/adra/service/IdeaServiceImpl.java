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


//    @Override
//    public List<Idea> findAllIdeasByChoice(String choice, int fetchNext) {
//        return ideaRepository.findAllIdeasByChoice(choice, fetchNext);
//    }
//
//    @Override
//    public Idea findIdeaByIdeaId(int id) throws IdeaCredentialsException {
//        return ideaRepository.findIdeaByIdeaId(id);
//    }
//
//    @Override
//    public List<IdeaReview> findReviewsByIdeaId(int id) throws IdeaCredentialsException {
//        return ideaRepository.findReviewsByIdeaId(id);
//    }
//
//    @Override
//    public void addIdeaReview(IdeaReview ideaReview) throws IdeaCredentialsException {
//        ideaRepository.addIdeaReview(ideaReview);
//    }

    @Override
    public void addIdea(Idea idea) throws IdeaCredentialsException {
        ideaRepository.addIdea(idea);
    }

//    @Override
//    public List<Idea> findIdeasByEmployeeIdAndChoice(int id, String choice, int fetchNext) {
//        return ideaRepository.findIdeasByEmployeeIdAndChoice(id, choice, fetchNext);
//    }
//
//    @Override
//    public void updateIdeaByIdeaId(Idea idea) throws IdeaCredentialsException {
//        ideaRepository.updateIdeaByIdeaId(idea);
//    }
//
//    @Override
//    public void deleteIdeaByIdeaId(int ideaId) throws IdeaCredentialsException {
//        ideaRepository.deleteIdeaByIdeaId(ideaId);
//    }
}
