package az.gov.adra.service.interfaces;

import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.exception.CommandCredentialsException;

import java.util.List;

public interface CommandService {

    List<CommandDTO> findAllCommands(int fetchNext);

    CommandDTO findCommandByCommandId(int id);

    void addCommand(Command command) throws CommandCredentialsException;

    void isCommandExistWithGivenId(int id) throws CommandCredentialsException;

}
