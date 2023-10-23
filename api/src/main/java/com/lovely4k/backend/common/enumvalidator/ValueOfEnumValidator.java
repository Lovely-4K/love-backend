package com.lovely4k.backend.common.enumvalidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValueOfEnumValidator implements ConstraintValidator<EnumValue, String> {

    private EnumValue enumValue;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumValue = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean result = false;
        Enum<?>[] enumValues = this.enumValue.enumClass().getEnumConstants();
        if (enumValues != null) {
            for (Object eVal : enumValues) {
                if (value.equals(eVal.toString())
                        || this.enumValue.ignoreCase() && value.equalsIgnoreCase(eVal.toString())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
