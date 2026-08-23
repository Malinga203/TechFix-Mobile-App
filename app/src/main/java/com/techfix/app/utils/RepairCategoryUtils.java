package com.techfix.app.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RepairCategoryUtils {

    public static final String TYPE_MOBILE =
            "MOBILE";

    public static final String TYPE_COMPUTER =
            "COMPUTER";


    private static final List<String> MOBILE_CATEGORIES =
            Collections.unmodifiableList(
                    Arrays.asList(
                            "Screen",
                            "Battery",
                            "Charging Port",
                            "Camera",
                            "Speaker",
                            "Microphone",
                            "Water Damage",
                            "Software",
                            "Diagnostics"
                    )
            );


    private static final List<String> COMPUTER_CATEGORIES =
            Collections.unmodifiableList(
                    Arrays.asList(
                            "Display",
                            "Keyboard",
                            "Battery",
                            "Storage",
                            "RAM",
                            "Motherboard",
                            "Power Supply",
                            "Cooling",
                            "Software",
                            "Diagnostics"
                    )
            );


    private RepairCategoryUtils() {
    }


    public static List<String> getMobileCategories() {

        return MOBILE_CATEGORIES;
    }


    public static List<String> getComputerCategories() {

        return COMPUTER_CATEGORIES;
    }


    public static List<String> getCategoriesForType(
            String type
    ) {

        if (
                TYPE_COMPUTER.equalsIgnoreCase(
                        type
                )
        ) {

            return COMPUTER_CATEGORIES;
        }

        return MOBILE_CATEGORIES;
    }


    public static String getDisplayType(
            String type
    ) {

        if (
                TYPE_COMPUTER.equalsIgnoreCase(
                        type
                )
        ) {

            return "Computer";
        }

        return "Mobile";
    }
}