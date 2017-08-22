package com.idn0phl3108ed43d22s30.etc;

import java.net.PortUnreachableException;

/**
 * Created by jimish on 18/6/16.
 */

public class Constants {
    public static final String SHARED_PREF_NAME = "OIZOM_USER_PREFS";
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String SHARED_ID = "userId";


    public static final String HTTP_PRE = "http://";
    public static final String AIROWL_IP_ADDRESS = "192.168.12.1";
    public static final String AIROWL_PORT_NUMBER = "8090";
    public static final String AIROWL_URL_SSID = "/data?ssid=";
    public static final String AIROWL_URL_PASS = "&pass=";
    public static final String PURCHASE_LINK = "http://www.oizom.com/";
    public static final String serverUrl = "http://api.oizom.com";

    public static final int AQI_GOOD_LOW = 0;
    public static final int AQI_GOOD_HIGH = 50;
    public static final int AQI_SATISFACTORY_LOW = 51;
    public static final int AQI_SATISFACTORY_HIGH = 100;
    public static final int AQI_MODERATE_LOW = 101;
    public static final int AQI_MODERATE_HIGH = 200;
    public static final int AQI_POOR_LOW = 201;
    public static final int AQI_POOR_HIGH = 300;
    public static final int AQI_VERY_POOR_LOW = 301;
    public static final int AQI_VERY_POOR_HIGH = 400;
    public static final int AQI_SEVERE_LOW = 401;
    public static final int AQI_SEVERE_HIGH = 500;

    public static final String BLOG_URL = "http://blog.oizom.com";
    public static final String DEVICE_URL = "http://oizom.com/polludrone-air-quality-monitoring";
    public static final String WEB_VIEW_MIME_TYPE = "text/html; charset=UTF-8";

    public static final String AIROWL_WEB_URL = "http://openenvironment.indiaopendata.com/#/airowl/";

    public static final String SPACES = "                                      ";

    //SQLIte helper constant..

    public static final String DB_NAME = "jimish5";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "USER";
    public static final String COLUMN_NAME_EMAIL = "EMAIL";
    public static final String COLUMN_NAME_PASSWORD = "PASSWORD";
    //Table devices public..
    public static final String TABLE_NAME_DEVICE = "DEVICE";
    public static final String COLUMN_NAME_ID = "_id";
    public static final String COLUMN_NAME_DETAIL = "detail_data";
    //Table devices private..
    public static final String TABLE_NAME_DEVICE_PRIVATE = "DEVICE_PRIVATE";
    public static final String COLUMN_NAME_ID_PRIVATE = "_id";
    public static final String COLUMN_NAME_DETAIL_PRIVATE = "detail_private";

    public static final double CENTER_LATITUDE = 23;
    public static final double CENTER_LONGITUDE = 72;
    public static final int ZOOM_LEVEL_MAP = 6;

    //database queries constants
    public static final String CREATE_TABLE = "CREATE TABLE ";
    public static final String TEXT_TYPE = " TEXT";
    public static final String TEXT_INTEGER = " INTEGER";
    public static final String TEXT_COMMA = ",";
    public static final String TEXT_PRIMARY_KEY = " PRIVATE KEY";
    public static final String PUBLIC_DATA_TABLE_NAME = "public_data";
    public static final String LAST_UPDATED_COLUMN_NAME = "last_updated";
    public static final String DATA_ARRAY_COLUMN_NAME = "data_array";
    public static final String ID_COLUMN = "_id";

    public static final String USER_DATA_TABLE_NAME = "user_data";
    public static final String USER_DATA_COLUMN_NAME = "data";

    public static final String PRIVATE_DATA_TABLE_NAME = "private_data";

    public static final String DROP_TABLE_TEXT= "DROP TABLE IF EXISTS ";

    public static final String PRIVATE_LATEST_DATA_TABLE = "latest_private_data";
    public static final String PUBLIC_LATEST_DATA_TABLE = "latest_public_data";

    public static final String AIR_QUALITY_INDIA_APP_HEADER = "air-quality-india-app";
    public static final String HEADER_VAL = "no-auth";

    public static final String MASHAPE_HEADER = "X-Mashape-Key";
    public static final String MASHAPE_VAL = "BFLZjYGs0Pmsh1GrZtU9LlvjlI6Rp1VxvKNjsnlArRsY03BAQX";

    public static final String ACCEPT_HEADER = "Accept";
    public static final String ACCEPT_H_VAL = "application/json";

    public static final String PUBLIC_SERVER_DOMAIN = "https://openenvironment.p.mashape.com";

    public static final String CITY_LIST_RESPONSE_KEY = "response";
    public static final String CITY_LIST_RESPONSE_KEY_MAP = "map";
    public boolean check = false;

    public static final String FACEBOOK_SECRET_KEY = "2ef267d813cdc44713909a960a1c21aa";
    public static final String FACEBOOK_APPID = "1183248778427503";
}
