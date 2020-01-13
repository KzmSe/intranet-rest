package az.gov.adra.exception.handler;

import az.gov.adra.entity.response.Exception;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.app-name}")
    private String appName;

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleDataAccessException(DataAccessException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0010");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("DataAccessException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "DataAccessException", exception);
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleNumberFormatException(NumberFormatException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0020");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("NumberFormatException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "NumberFormatException", exception);
    }

    @ExceptionHandler(ActivityCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleActivityCredentialsException(ActivityCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0030");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("ActivityCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "ActivityCredentialsException", exception);
    }

    @ExceptionHandler(UserCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleUserCredentialsException(UserCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0040");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("UserCredentialsException.");

        return GenericResponse.withException(HttpStatus.BAD_REQUEST, "UserCredentialsException", exception);
    }

    @ExceptionHandler(PostCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handlePostCredentialsException(PostCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0050");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("PostCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "PostCredentialsException", exception);
    }

    @ExceptionHandler(AnnouncementCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleAnnouncementCredentialsException(AnnouncementCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0060");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("AnnouncementCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "AnnouncementCredentialsException", exception);
    }

    @ExceptionHandler(CommandCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleCommandCredentialsException(CommandCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0070");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("CommandCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "CommandCredentialsException", exception);
    }

    @ExceptionHandler(DocumentCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleDocumentCredentialsException(DocumentCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0080");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("DocumentCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "DocumentCredentialsException", exception);
    }

    @ExceptionHandler(IdeaCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleIdeaCredentialsException(IdeaCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0090");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("IdeaCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "IdeaCredentialsException", exception);
    }

    @ExceptionHandler(DepartmentCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleDepartmentCredentialsException(DepartmentCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0100");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("DepartmentCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "DepartmentCredentialsException", exception);
    }

    @ExceptionHandler(SectionCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleSectionCredentialsException(SectionCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0110");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("SectionCredentialsException.");

        return GenericResponse.withException(HttpStatus.INTERNAL_SERVER_ERROR, "SectionCredentialsException", exception);
    }

}
