package com.jefff.app.restful.web.exceptions;

import com.jefff.app.restful.web.ui.model.response.ErrorMessages;

import java.io.Serializable;

public class UserServiceException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -8696021384796533311L;

    public UserServiceException(ErrorMessages code) {
        this(code.getErrorMessage());
    }
    public UserServiceException(String message) {
        super(message);
    }
}
