package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Idea;
import az.gov.adra.exception.IdeaCredentialsException;

import java.util.List;

public interface IdeaRepository {

//    List<Idea> findAllIdeasByChoice(String choice, int fetchNext);
//
//    Idea findIdeaByIdeaId(int id) throws IdeaCredentialsException;
//
//    List<IdeaReview> findReviewsByIdeaId(int id) throws IdeaCredentialsException;
//
//    void addIdeaReview(IdeaReview ideaReview) throws IdeaCredentialsException;

    void addIdea(Idea idea) throws IdeaCredentialsException;

//    List<Idea> findIdeasByEmployeeIdAndChoice(int id, String choice, int fetchNext);
//
//    void updateIdeaByIdeaId(Idea idea) throws IdeaCredentialsException;
//
//    void deleteIdeaByIdeaId(int ideaId) throws IdeaCredentialsException;

}