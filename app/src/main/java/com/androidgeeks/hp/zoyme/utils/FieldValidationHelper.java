package com.androidgeeks.hp.zoyme.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deepak Sikka on 10/25/2017.
 */

public class FieldValidationHelper {


    public static boolean isValidName(String text) {
        boolean valid_name = false;
        try {
            if (text.length() <= 0) {

                valid_name = false;
            }
            else if (!text.matches("[a-zA-Z.\\' ]+")) {

                valid_name = false;
            }
            else {
                valid_name = true;
            }
        } catch (Exception e) {

        }
        return valid_name;

    }

    public final static boolean isValidEmail(String emailAddress) {
        if (emailAddress == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress)
                    .matches();
        }
    }




    public final static boolean isValidPhoneNumber(String phone)
    {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    public final static boolean isPasswordMatching(String password, String confirmPassword) {
        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirmPassword);

        if (!matcher.matches()) {
            // do your Toast("passwords are not matching");

            return false;
        }

        return true;
    }
}
