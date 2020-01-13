package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Idea;
import az.gov.adra.exception.IdeaCredentialsException;

import java.util.List;

public interface IdeaRepository {

    void addIdea(Idea idea) throws IdeaCredentialsException;

}
