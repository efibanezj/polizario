package com.ij.polizario.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class YpExceptionMessage {

    private final MessageSource messageSource;

    public YpExceptionMessage(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Retrieves the exception message using their {@link BusinessExceptionEnum#getValue()}.
     *
     * @param code the exception error code.
     * @return the internationalized message.
     */
    public String getMessage(int code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(String.valueOf(code), null, locale);
    }

    /**
     * Retrieves the exception message using their name.
     * <p>
     * The i18n is a key added to {@link BusinessExceptionEnum} to use for JSR 303 specification validations.
     *
     * @param i18n the key for the exception message.
     * @return the internationalized message.
     */
    public String getMessage(String i18n) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(i18n, null, locale);
    }
}
