package com.ij.polizario.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessExceptionEnum {

    /*************************************
     * HTTP REQUEST ISSUES
     * 100-599
     *************************************/

    REQUEST_INPUT_ERROR(400),
    SERVER_ERROR(500),

    HTTP_MESSAGE_NOT_READABLE(4001),

    /*************************************
     * Business errors
     * 30000-39999
     *************************************/

    IDENTITY_ALREADY_EXIST(30000 )


    ;

    private final int value;

    private static final Map<Integer, BusinessExceptionEnum> map = new HashMap<>();

    static {
        for (BusinessExceptionEnum ypExceptionEnum : BusinessExceptionEnum.values()) {
            map.put(ypExceptionEnum.value, ypExceptionEnum);
        }
    }

    public static BusinessExceptionEnum valueOf(int value) {
        return map.get(value);
    }
}
