package com.ij.polizario.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class YpExceptionResponse {

    /**
     * This error code should provide details as to what actually caused the exception
     */
    private int errorCode;

    /**
     * Error code from external source if applicable
     */
    private String externalErrorCode;

    /**
     * Additional exception information This should probably not be exposed in a production environment.
     */
    private List<String> errorMessages = new ArrayList<>();

    public YpExceptionResponse(int errorCode, String externalErrorCode, String... errorMessages) {
        this.errorCode = errorCode;
        this.externalErrorCode = externalErrorCode;
        this.errorMessages = Arrays.asList(errorMessages);
    }

}