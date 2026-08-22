package com.techfix.app.userauthentication.utils;

import android.util.Patterns;

public class ValidationUtils {

    private ValidationUtils() {
        // Prevent creating objects of this utility class
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        String cleanedPhone = phone.trim();

        return cleanedPhone.matches("\\d{10}");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean passwordsMatch(
            String password,
            String confirmPassword) {

        if (password == null || confirmPassword == null) {
            return false;
        }

        return password.equals(confirmPassword);
    }
}