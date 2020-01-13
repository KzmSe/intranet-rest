package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.exception.CommandCredentialsException;

import java.util.List;

public interface CommandRepository {

    List<CommandDTO> findAllCommands(int offset);

    CommandDTO findCommandByCommandId(int id);

    List<CommandDTO> findTopThreeCommandsByLastAddedTime();

    void addCommand(Command command) throws CommandCredentialsException;

    int findCountOfAllCommands();

    void isCommandExistWithGivenId(int id) throws CommandCredentialsException;

}
