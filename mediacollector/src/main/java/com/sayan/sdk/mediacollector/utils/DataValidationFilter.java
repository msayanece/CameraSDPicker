package com.sayan.sdk.mediacollector.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidationFilter {

    public static boolean isNotNullObject(Object object){
        return object != null;
    }

    public static boolean isValidList(List list){
        return list != null && !list.isEmpty();
    }

    public static boolean isValidString(String string) {
        return string != null && !(string.isEmpty() || string.equalsIgnoreCase("null"));
    }

    public static boolean isIntValueNotZero(int value) {
        return value != 0;
    }

    public static class InputTextValidationFilter{

        public static boolean validatePassword(String password) {
            return !password.isEmpty() && password.length() >= 4 && password.length() <= 8;
        }

        public static String getPasswordValidationError(String password) {
            if (password.isEmpty()){
                return "please enter password";
            }
            else if (password.length() <4){
                return "password should be minimum 4 characters";
            }
            else if (password.length() >8){
                return "password should be maximum 8 characters";
            }
            else {
                // This should never happen
                return "No Error";
            }
        }

        public static boolean isValidEmail(String email) {
            int count = 0;
            for(int i=0;i<email.length();++i)
            {
                if(email.charAt(i)=='@')
                    count++;
            }

            if(count>1){
                return false;
            }else{
                return email.contains("@") && email.contains(".");
            }

        }

        public static boolean isValidMobile(String sMobileNo) {
            if (sMobileNo.contains("+")) {
                return sMobileNo.length() == 13;
            } else {
                return sMobileNo.length() == 10;
            }
        }

        public static boolean isValidPan(String pan) {
            Pattern mPattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
            Matcher mMatcher = mPattern.matcher(pan);
            return mMatcher.matches();
        }

    }




}
