package az.gov.adra.exception.handler;

import az.gov.adra.entity.response.Exception;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.exception.*;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleDataAccessException(DataAccessException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0010");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("DataAccessException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("DataAccessException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleNumberFormatException(NumberFormatException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0020");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("NumberFormatException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("NumberFormatException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(ActivityCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleActivityCredentialsException(ActivityCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0030");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("ActivityCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("ActivityCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(UserCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleUserCredentialsException(UserCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0040");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("UserCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withDescription("UserCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(PostCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handlePostCredentialsException(PostCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0050");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("PostCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("PostCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(AnnouncementCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleAnnouncementCredentialsException(AnnouncementCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0060");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("AnnouncementCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("AnnouncementCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(CommandCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleCommandCredentialsException(CommandCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0070");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("CommandCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("CommandCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(DocumentCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleDocumentCredentialsException(DocumentCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0080");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("DocumentCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("DocumentCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(IdeaCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleIdeaCredentialsException(IdeaCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0090");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("IdeaCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("IdeaCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(DepartmentCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleDepartmentCredentialsException(DepartmentCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0100");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("DepartmentCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("DepartmentCredentialsException")
                .withException(exception)
                .build();
    }

    @ExceptionHandler(SectionCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleSectionCredentialsException(SectionCredentialsException e) {
        //Exception
        Exception exception = new Exception();
        exception.setCode("0x0110");
        exception.setMessage(e.getMessage());
        exception.setErrorStack("SectionCredentialsException.");

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDescription("SectionCredentialsException")
                .withException(exception)
                .build();
    }

}
