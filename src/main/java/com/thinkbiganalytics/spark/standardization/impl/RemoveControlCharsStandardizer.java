/*
 * Copyright (c) 2016. Teradata Inc.
 */

package com.thinkbiganalytics.spark.standardization.impl;

/**
 * Removes all control characters from a strings including newlines, tabs
 */
public class RemoveControlCharsStandardizer extends SimpleRegexReplacer {

    private static final RemoveControlCharsStandardizer instance = new RemoveControlCharsStandardizer();

    private RemoveControlCharsStandardizer() {
        super("\\p{Cc}", "");
    }

    public static RemoveControlCharsStandardizer instance() {
        return instance;
    }


    public static void main(String[] args) {
        RemoveControlCharsStandardizer c = RemoveControlCharsStandardizer.instance();
        System.out.println(c.convertValue("a\u0000b\u0007c\u008fd\ne"));
    }
}
