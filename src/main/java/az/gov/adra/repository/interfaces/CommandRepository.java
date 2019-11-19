package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.exception.CommandCredentialsException;

import java.util.List;

public interface CommandRepository {

    List<CommandDTO> findAllCommands(int fetchNext);

    CommandDTO findCommandByCommandId(int id);

    void addCommand(Command command) throws CommandCredentialsException;

    void isCommandExistWithGivenId(int id) throws CommandCredentialsException;

}
