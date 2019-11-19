package az.gov.adra.service;

import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.exception.CommandCredentialsException;
import az.gov.adra.repository.interfaces.CommandRepository;
import az.gov.adra.service.interfaces.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandServiceImpl implements CommandService {

    private CommandRepository commandRepository;

    @Autowired
    public CommandServiceImpl(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @Override
    public List<CommandDTO> findAllCommands(int fetchNext) {
        return commandRepository.findAllCommands(fetchNext);
    }

    @Override
    public CommandDTO findCommandByCommandId(int id) {
        return commandRepository.findCommandByCommandId(id);
    }

    @Override
    public void addCommand(Command command) throws CommandCredentialsException {
        commandRepository.addCommand(command);
    }

    @Override
    public void isCommandExistWithGivenId(int id) throws CommandCredentialsException {
        commandRepository.isCommandExistWithGivenId(id);
    }

}
