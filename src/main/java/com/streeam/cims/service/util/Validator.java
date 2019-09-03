package com.streeam.cims.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {

    private static final String emailRegex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";

    private Validator(){};

    public static boolean validateEmail(String email){

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return !matcher.matches();
    }
}
