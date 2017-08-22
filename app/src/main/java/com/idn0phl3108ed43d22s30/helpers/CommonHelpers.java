package com.idn0phl3108ed43d22s30.helpers;

import com.idn0phl3108ed43d22s30.etc.Constants;

/**
 * Created by jimish on 22/9/16.
 */

public class CommonHelpers {

    public static String getStatus(int aqiIntVal) {
        String retStatus = "";
        if (aqiIntVal >= Constants.AQI_GOOD_LOW && aqiIntVal <= Constants.AQI_GOOD_HIGH) {
            retStatus = "Good";
        } else if (aqiIntVal >= Constants.AQI_SATISFACTORY_LOW && aqiIntVal <= Constants.AQI_SATISFACTORY_HIGH) {
            retStatus = "Satisfactory";
        } else if (aqiIntVal >= Constants.AQI_MODERATE_LOW && aqiIntVal <= Constants.AQI_MODERATE_HIGH) {
            retStatus = "Moderate";
        } else if (aqiIntVal >= Constants.AQI_POOR_LOW && aqiIntVal <= Constants.AQI_POOR_HIGH) {
            retStatus = "Poor";
        } else if (aqiIntVal >= Constants.AQI_VERY_POOR_LOW && aqiIntVal <= Constants.AQI_VERY_POOR_HIGH) {
            retStatus = "Very Poor";
        } else if (aqiIntVal >= Constants.AQI_SEVERE_LOW) {
            retStatus = "Severe";
        } else {
            retStatus = "Satisfactory";
        }
        return retStatus;
    }
}
