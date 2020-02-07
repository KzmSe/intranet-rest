package az.gov.adra.service.interfaces;

import az.gov.adra.entity.Idea;
import az.gov.adra.exception.IdeaCredentialsException;

public interface IdeaService {

    void addIdea(Idea idea) throws IdeaCredentialsException;

    int findCountOfAllIdeas();

}
