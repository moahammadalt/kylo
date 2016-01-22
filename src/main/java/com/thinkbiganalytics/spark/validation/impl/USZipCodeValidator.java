/*
 * Copyright (c) 2016. Teradata Inc.
 */

package com.thinkbiganalytics.spark.validation.impl;

import com.thinkbiganalytics.spark.validation.Validator;

/**
 * Validates US phone numbers
 */
public class USZipCodeValidator extends RegexValidator implements Validator<String> {

    private static final USZipCodeValidator instance = new USZipCodeValidator();

    private USZipCodeValidator() {
        super("[0-9]{5}([- /]?[0-9]{4})?$");
    }

    public static USZipCodeValidator instance() {
        return instance;
    }

}