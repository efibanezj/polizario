package com.ij.polizario.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This exception is for use when a bad state occurs because of some internal problem. It is not recommended for use
 * with external resources(Web services, rest calls, etc).
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * This error code should provide details as to what actually caused the exception
     */
    private int errorCode;

    /**
     * Create exception with only internal exception values (error codes and messages that do not come directly from a
     * third party or external source)
     *
     * @param clientErrorCode - Internal error code mapped to internal error message The exception message that this
     * error code maps to should not be null
     */
    public BusinessException(BusinessExceptionEnum clientErrorCode) {
        super(clientErrorCode.name());
        this.errorCode = clientErrorCode.getValue();
    }

    public BusinessException(String message) {
        super(message);
    }

}