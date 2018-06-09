package com.minibox.exception;

import com.aliyuncs.exceptions.ClientException;
import com.minibox.dto.ResponseEntity;
import com.minibox.constants.ExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;


@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger logger= LoggerFactory.getLogger("exception");

    private static final int SERVER_EXCEPTION_CODE = 500;
    private static final int CLIENT_EXCEPTION_CODE = 400;
    private static final int RESOURCE_NOT_FOUND_EXCEPTION_CODE = 404;
    private static final int HARDWARE_EXCEPTION_CODE = 501;

    @ExceptionHandler(ParameterException.class)
    public ResponseEntity<Object> handlerParameterException(ParameterException e) {
        e.printStackTrace();
        return new ResponseEntity<>(ParameterException.STATUS, e.getMessage(), null);
    }

    @ExceptionHandler(RollbackException.class)
    public ResponseEntity<Object> handlerRollbackException(RollbackException e) {
        e.printStackTrace();
        return new ResponseEntity<>(RollbackException.STATUS, e.getMessage(), null);
    }

    @ExceptionHandler(SendSmsFailedException.class)
    public ResponseEntity<Object> handlerSendSmsFailedException(SendSmsFailedException e) {
        e.printStackTrace();
        return new ResponseEntity<>(SendSmsFailedException.STATUS, e.getMessage(), null);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<Object> handlerServerException(ServerException e) {
        logger.warn(e.getStackTrace().toString());
        return new ResponseEntity<>(ServerException.STATUS, e.getMessage(), null);
    }

    @ExceptionHandler(TokenVerifyException.class)
    public ResponseEntity<Object> handlerTokenVerifyException(TokenVerifyException e) {
        e.printStackTrace();
        return new ResponseEntity<>(TokenVerifyException.STATUS, ExceptionMessage.AUTHENTICATION_FAILURE, null);
    }

    @ExceptionHandler(VerifyCodeException.class)
    public ResponseEntity<Object> handlerVerifyCodeException(VerifyCodeException e) {
        e.printStackTrace();
        return new ResponseEntity<>(VerifyCodeException.STATUS, e.getMessage(), null);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<Object> handlerClientException(ClientException e) {
        e.printStackTrace();
        return new ResponseEntity<>(CLIENT_EXCEPTION_CODE, "客户端请求错误", null);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handlerNullPointerException(NullPointerException e) {
        e.printStackTrace();
        return new ResponseEntity<>(RESOURCE_NOT_FOUND_EXCEPTION_CODE, e.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handlerResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(ResourceNotFoundException.STATUS, e.getMessage(), null);
    }

    @ExceptionHandler(HardwareException.class)
    public ResponseEntity<Object> handlerHardwareException(HardwareException e) {
        return new ResponseEntity<>(HARDWARE_EXCEPTION_CODE, e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handlerException(Exception e) {
        logger.warn(Arrays.toString(e.getStackTrace()));
        System.out.println(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>(SERVER_EXCEPTION_CODE, "未知错误", null);
    }
}
