package com.idn0phl3108ed43d22s30.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.idn0phl3108ed43d22s30.etc.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Rutul on 19-07-2016.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteDatabaseHelper.class.getSimpleName();
    private static final String COLUMN_ID = " _ID";

    //create table query for user data save
    public static final String  CREATE_USER_DATA = Constants.CREATE_TABLE + Constants.USER_DATA_TABLE_NAME +
            " ("+ Constants.ID_COLUMN + " " + Constants.TEXT_INTEGER + Constants.TEXT_PRIMARY_KEY +
            Constants.TEXT_COMMA + " " + Constants.USER_DATA_COLUMN_NAME + Constants.TEXT_TYPE +
            Constants.TEXT_COMMA + " " + Constants.LAST_UPDATED_COLUMN_NAME + Constants.TEXT_TYPE +
            ")";

    //create table query for public data save
    public static final String  CREATE_PUBLIC_DATA = Constants.CREATE_TABLE + Constants.PUBLIC_DATA_TABLE_NAME +
            " ("+ Constants.ID_COLUMN + " " + Constants.TEXT_INTEGER + Constants.TEXT_PRIMARY_KEY +
            Constants.TEXT_COMMA + " " + Constants.DATA_ARRAY_COLUMN_NAME + Constants.TEXT_TYPE +
            Constants.TEXT_COMMA + " " + Constants.LAST_UPDATED_COLUMN_NAME + Constants.TEXT_TYPE +
            ")";

    //create table query for private data save
    public static final String  CREATE_PRIVATE_TABLE = Constants.CREATE_TABLE + Constants.PRIVATE_DATA_TABLE_NAME +
            " ("+ Constants.ID_COLUMN + " " + Constants.TEXT_INTEGER + Constants.TEXT_PRIMARY_KEY +
            Constants.TEXT_COMMA + " " + Constants.DATA_ARRAY_COLUMN_NAME + Constants.TEXT_TYPE +
            Constants.TEXT_COMMA + " " + Constants.LAST_UPDATED_COLUMN_NAME + Constants.TEXT_TYPE +
            ")";

    //create latest data query for private data
    public static final String CREATE_LATEST_PRIVATE_TABLE = Constants.CREATE_TABLE + Constants.PRIVATE_LATEST_DATA_TABLE +
            " (" + Constants.ID_COLUMN + " " + Constants.TEXT_INTEGER + Constants.TEXT_PRIMARY_KEY +
            Constants.TEXT_COMMA + " " + Constants.DATA_ARRAY_COLUMN_NAME + Constants.TEXT_TYPE +
            Constants.TEXT_COMMA + " " + Constants.LAST_UPDATED_COLUMN_NAME + Constants.TEXT_TYPE + ")";

    //create latest data query for public data
    public static final String CREATE_LATEST_PUBLIC_TABLE = Constants.CREATE_TABLE + Constants.PUBLIC_LATEST_DATA_TABLE +
            " (" + Constants.ID_COLUMN + " " + Constants.TEXT_INTEGER + Constants.TEXT_PRIMARY_KEY +
            Constants.TEXT_COMMA + " " + Constants.DATA_ARRAY_COLUMN_NAME + Constants.TEXT_TYPE +
            Constants.TEXT_COMMA + " " + Constants.LAST_UPDATED_COLUMN_NAME + Constants.TEXT_TYPE + ")";

    //delete table query for user data
    public static final String DELETE_TABLE_USER = Constants.DROP_TABLE_TEXT + Constants.USER_DATA_TABLE_NAME;

    //delete table query for private data
    public static final String DELETE_TABLE_PRIVATE_DATA = Constants.DROP_TABLE_TEXT + Constants.PRIVATE_DATA_TABLE_NAME;

    //delete table query for public data
    public static final String DELETE_TABLE_PUBLIC_DATA = Constants.DROP_TABLE_TEXT + Constants.PUBLIC_DATA_TABLE_NAME;

    //delete table query for latest private data
    public static final String DELETE_LATEST_PRIVATE_DATA = Constants.DROP_TABLE_TEXT + Constants.PRIVATE_LATEST_DATA_TABLE;

    //delete table query for latest public data
    public static final String DELETE_LATEST_PUBLIC_DATA = Constants.DROP_TABLE_TEXT + Constants.PUBLIC_LATEST_DATA_TABLE;


    public SQLiteDatabaseHelper(Context context) {
        //null is constructor factory..
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRIVATE_TABLE);
        db.execSQL(CREATE_PUBLIC_DATA);
        db.execSQL(CREATE_USER_DATA);
        db.execSQL(CREATE_LATEST_PRIVATE_TABLE);
        db.execSQL(CREATE_LATEST_PUBLIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertDataToUserTable(JSONObject jsonObject){
        String user_data = jsonObject.toString();
        if(user_data!=null && !(user_data.isEmpty())){
            //get writable database
            SQLiteDatabase db = this.getWritableDatabase();

            //current time
            Date date = new Date();
            String currentTime = String.valueOf(date.getTime());

            //prepare contents to put in user_table
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.COLUMN_NAME_ID, 1);
            contentValues.put(Constants.USER_DATA_COLUMN_NAME, user_data);
            contentValues.put(Constants.LAST_UPDATED_COLUMN_NAME, currentTime);

            //insert to db
            db.insert(Constants.USER_DATA_TABLE_NAME, null, contentValues);
            db.close();

            Log.i(TAG, "updated user data successfully at epoch time "+currentTime);
        } else {
            Log.d(TAG, "null values can not be inserted to user data table.");
        }
    }

    public JSONObject readUserDataValue(){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] column_details = {
                Constants.USER_DATA_COLUMN_NAME
        };
        Cursor cursor = db.query(Constants.USER_DATA_TABLE_NAME,column_details,null,null,
                null,null,null);

        String returnData = null;

        if(cursor.getCount()<0){
            cursor.moveToFirst();
            returnData = cursor.getString(cursor.getColumnIndex(Constants.USER_DATA_COLUMN_NAME));
        }

        JSONObject returnJSON;
        try{
            if(returnData!=null && !(returnData.isEmpty())){
                returnJSON = new JSONObject(returnData);
                Log.i(TAG, "returning json object from user data table");
            } else {
                returnJSON = null;
                Log.d(TAG, "returning null data from user data table");
            }
        } catch (JSONException e){
            returnJSON = null;
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
            Log.d(TAG, "returning null data from user data table");
        }

        return returnJSON;

    }

    public void updateUserValue(JSONObject jsonObject){
        if(jsonObject!=null){
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(DELETE_TABLE_USER);

            db.execSQL(CREATE_USER_DATA);

            insertDataToUserTable(jsonObject);
            Log.i(TAG, "successfully updated json object to user data");
        } else {
            Log.d(TAG, "null data can not be updated to user data table");
        }

    }

    public void insertPublicDataArray(JSONArray jsonArray){
        String public_data = jsonArray.toString();

        if(public_data!=null && !(public_data.isEmpty())){
            //get writable database
            SQLiteDatabase db = this.getWritableDatabase();

            //current time
            Date date = new Date();
            String currentTime = String.valueOf(date.getTime());

            //prepare contents to put in user_table
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.COLUMN_NAME_ID, 1);
            contentValues.put(Constants.DATA_ARRAY_COLUMN_NAME, public_data);
            contentValues.put(Constants.LAST_UPDATED_COLUMN_NAME, currentTime);

            //insert value in db
            db.insert(Constants.PUBLIC_DATA_TABLE_NAME, null, contentValues);
            db.close();

            Log.i(TAG, "successfully inserted json array to public data table");
        } else {
            Log.d(TAG, "null data can not be inserted to public data table");
        }
    }

    public JSONArray udpateJSONArrayWithObj(JSONArray jsonArray, JSONObject jsonObject){
        boolean flag = false;
        int array_size = jsonArray.length();
        try{
            for(int i=0; i<array_size; i++){
                if(jsonArray.getJSONObject(i).getString("deviceId").equals(jsonObject.getString("deviceId"))){
                    jsonArray.put(i, jsonObject);
                    flag = true;
                    break;
                }
            }
            if(!flag){
                jsonArray.put(array_size,jsonObject);
            }
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }

        return jsonArray;
    }

    public JSONArray readPublicDataValue(){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] column_details = {
                Constants.DATA_ARRAY_COLUMN_NAME
        };
        Cursor cursor = db.query(Constants.PUBLIC_DATA_TABLE_NAME,column_details,null,null,
                null,null,null);

        String returnData = null;

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            returnData = cursor.getString(cursor.getColumnIndex(Constants.DATA_ARRAY_COLUMN_NAME));
        } else {
            returnData = null;
        }

        JSONArray returnJSON;
        try{
            if(returnData!=null && !(returnData.isEmpty())){
                returnJSON = new JSONArray(returnData);
                Log.i(TAG, "returning json array of public data");
            } else {
                returnJSON = null;
                Log.i(TAG, "no public data available in database");
            }
        } catch (JSONException e){
            returnJSON = null;
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
            Log.i(TAG, "no public data available in database");
        }

        return returnJSON;

    }

    public void updatePublicData(JSONObject jsonObject){
        if(jsonObject!=null){
            JSONArray jsonArray = readPublicDataValue();

            if(jsonArray==null){
                jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                insertPublicDataArray(jsonArray);
            } else {
                jsonArray = udpateJSONArrayWithObj(jsonArray, jsonObject);

                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL(DELETE_TABLE_PUBLIC_DATA);

                db.execSQL(CREATE_PUBLIC_DATA);

                insertPublicDataArray(jsonArray);
            }


            Log.i(TAG, "updated public data successfully!");
        } else {
            Log.e(TAG, "null data can not be updated to public data");
        }
    }

    public boolean isPublicDeviceRegistered(JSONObject jsonObject){
        try{
            if(jsonObject != null){
                JSONArray publicArray = readPublicDataValue();
                String srcDeviceId = jsonObject.getString("deviceId");
                if(publicArray != null){
                    int publicArraySize = publicArray.length();
                    if(publicArraySize > 0){
                        for(int i=0;i<publicArraySize;i++){
                            JSONObject toCheckDeviceObj = publicArray.getJSONObject(i);
                            if(toCheckDeviceObj.getString("deviceId").equals(srcDeviceId)){
                                publicArray.put(i,jsonObject);

                                SQLiteDatabase db = this.getWritableDatabase();
                                db.execSQL(DELETE_TABLE_PUBLIC_DATA);
                                db.execSQL(CREATE_PUBLIC_DATA);

                                insertPublicDataArray(publicArray);
                                return true;
                            }
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }

        return false;
    }

    public void insertPrivateDataArray(JSONArray jsonArray){
        String private_data = jsonArray.toString();

        if(private_data!=null && !(private_data.isEmpty())){
            //get writable database
            SQLiteDatabase db = this.getWritableDatabase();

            //current time
            Date date = new Date();
            String currentTime = String.valueOf(date.getTime());

            //prepare contents to put in user_table
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.COLUMN_NAME_ID, 1);
            contentValues.put(Constants.DATA_ARRAY_COLUMN_NAME, private_data);
            contentValues.put(Constants.LAST_UPDATED_COLUMN_NAME, currentTime);

            //insert value in db
            db.insert(Constants.PRIVATE_DATA_TABLE_NAME, null, contentValues);
            db.close();

            Log.i(TAG, "successfully inserted json array to private data");
        } else {
            Log.d(TAG, "null data can not be inserted to private data table");
        }
    }

    public JSONArray readPrivateDataValue(){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        String[] column_details = {
                Constants.DATA_ARRAY_COLUMN_NAME
        };
        Cursor cursor = db.query(Constants.PRIVATE_DATA_TABLE_NAME,column_details,null,null,
                null,null,null);

        String returnData = null;

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            returnData = cursor.getString(cursor.getColumnIndex(Constants.DATA_ARRAY_COLUMN_NAME));
        } else {
            returnData = null;
        }

        JSONArray returnJSON;
        try{
            if(returnData!=null && !(returnData.isEmpty())){
                returnJSON = new JSONArray(returnData);
                Log.i(TAG, "returning json array of private data");
            } else {
                returnJSON = null;
                Log.i(TAG, "no private data available in database");
            }
        } catch (JSONException e){
            returnJSON = null;
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
            Log.i(TAG, "no private data available in database");
        }

        return returnJSON;

    }

    public void updatePrivateData(JSONObject jsonObject){
        if(jsonObject!=null){
            JSONArray jsonArray = readPrivateDataValue();

            if(jsonArray == null){
                jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                insertPrivateDataArray(jsonArray);
            } else {
                jsonArray = udpateJSONArrayWithObj(jsonArray, jsonObject);

                SQLiteDatabase db = this.getWritableDatabase();
                db.execSQL(DELETE_TABLE_PRIVATE_DATA);

                db.execSQL(CREATE_PRIVATE_TABLE);

                insertPrivateDataArray(jsonArray);
            }


            Log.i(TAG, "updated private data successfully!");
        } else {
            Log.e(TAG, "null data can not be updated to private data");
        }
    }

    public boolean isPrivateDeviceRegistered(JSONObject jsonObject){
        try{
            if(jsonObject != null){
                JSONArray privateArray = readPrivateDataValue();
                String srcDeviceId = jsonObject.getString("deviceId");
                if(privateArray != null){
                    int publicArraySize = privateArray.length();
                    if(publicArraySize > 0){
                        for(int i=0;i<publicArraySize;i++){
                            JSONObject toCheckDeviceObj = privateArray.getJSONObject(i);
                            if(toCheckDeviceObj.getString("deviceId").equals(srcDeviceId)){
                                privateArray.put(i,jsonObject);

                                SQLiteDatabase db = this.getWritableDatabase();
                                db.execSQL(DELETE_TABLE_PRIVATE_DATA);
                                db.execSQL(CREATE_PRIVATE_TABLE);

                                insertPublicDataArray(privateArray);
                                return true;
                            }
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }

        return false;
    }


    public void insertLatestPrivateObj(JSONObject jsonObject){
        String private_data = jsonObject.toString();
        if(private_data != null && !(private_data.isEmpty())){
            //get writable database
            SQLiteDatabase db = this.getWritableDatabase();

            //get current time in string
            Date date = new Date();
            String currentTime = String.valueOf(date.getTime());

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.ID_COLUMN, 1);
            contentValues.put(Constants.DATA_ARRAY_COLUMN_NAME, private_data);
            contentValues.put(Constants.LAST_UPDATED_COLUMN_NAME, currentTime);

            //insert value to table
            db.insert(Constants.PRIVATE_LATEST_DATA_TABLE, null, contentValues);
            db.close();

            Log.i(TAG, "successfully inserted private latest value to table at : "+currentTime);
        } else {
            Log.d(TAG, "null data can not be inserted to private latest data.");
        }
    }

    public JSONObject readLatestPrivateObj(){
        JSONObject returnObj = null;
        try{
            //get readable database
            SQLiteDatabase db = this.getReadableDatabase();

            //cursor for db
            String[] column_details = {
                    Constants.DATA_ARRAY_COLUMN_NAME
            };
            Cursor cursor = db.query(Constants.PRIVATE_LATEST_DATA_TABLE,column_details,null,null,
                    null,null,null);

            String returnData = null;

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                returnData = cursor.getString(cursor.getColumnIndex(Constants.DATA_ARRAY_COLUMN_NAME));
            } else {
                returnData = null;
            }

            if(returnData!=null && !(returnData.isEmpty())){
                returnObj = new JSONObject(returnData);
                Log.i(TAG, "returning data from private latest data table successfully");
            } else {
                returnObj = null;
                Log.d(TAG, "can not access data from latest private data table");
            }
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
            Log.e(TAG, "can not access data from latest private data table");
        }

        return returnObj;
    }

    public void updateLatestPrivateObj(JSONObject jsonObject){
        if(jsonObject != null){
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL(DELETE_LATEST_PRIVATE_DATA);

            db.execSQL(CREATE_LATEST_PRIVATE_TABLE);
            insertLatestPrivateObj(jsonObject);
            Log.i(TAG, "succesfully updated latest private data table");
        } else {
            Log.d(TAG, "null json object can not be updated in latest private data table");
        }
    }

    public void updateLatestPrivateObjDevieId(String deviceId){
        try{
            JSONArray jsonArray = readPrivateDataValue();
            if(deviceId!=null && !(deviceId.isEmpty())){
                for(int i=0;i<jsonArray.length();i++){
                    if(jsonArray.getJSONObject(i).getString("deviceId").equals(deviceId)){
                        updateLatestPrivateObj(jsonArray.getJSONObject(i));
                    }
                }
            }
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }

    }

    public void insertLatestPublicObj(JSONObject jsonObject){
        String public_data = jsonObject.toString();
        if(public_data != null && !(public_data.isEmpty())){
            //get writable database
            SQLiteDatabase db = this.getWritableDatabase();

            //get current time
            Date date = new Date();
            String currentTime = String.valueOf(date.getTime());

            //prepare content values for inserting
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.ID_COLUMN, 1);
            contentValues.put(Constants.DATA_ARRAY_COLUMN_NAME, public_data);
            contentValues.put(Constants.LAST_UPDATED_COLUMN_NAME, currentTime);

            //insert data to table
            db.insert(Constants.PUBLIC_LATEST_DATA_TABLE, null, contentValues);
            db.close();

            Log.i(TAG, "successfully inserted data to latest public data table at epoch time : "+currentTime);
        } else {
            Log.d(TAG, "Can not insert null data object to latest public data table");
        }
    }

    public JSONObject readLatestPublicObj(){
        JSONObject returnObj = null;
        try{
            //get readable database
            SQLiteDatabase db = this.getReadableDatabase();

            //cursor for db
            String[] column_details = {
                    Constants.DATA_ARRAY_COLUMN_NAME
            };
            Cursor cursor = db.query(Constants.PUBLIC_LATEST_DATA_TABLE,column_details,null,null,
                    null,null,null);

            String returnData = null;

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                returnData = cursor.getString(cursor.getColumnIndex(Constants.DATA_ARRAY_COLUMN_NAME));
            } else {
                returnData = null;
            }

            if(returnData!=null && !(returnData.isEmpty())){
                returnObj = new JSONObject(returnData);
                Log.i(TAG, "returning data from public latest data table successfully");
            } else {
                returnObj = null;
                Log.d(TAG, "can not access data from latest public data table");
            }
        } catch (JSONException e){
            Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
        }

        return returnObj;
    }

    public void updateLatestPublicObj(JSONObject jsonObject){
        if(jsonObject != null){
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL(DELETE_LATEST_PUBLIC_DATA);

            db.execSQL(CREATE_LATEST_PUBLIC_TABLE);
            insertLatestPublicObj(jsonObject);
            Log.i(TAG, "successfully updated latest public data table");
        } else {
            Log.d(TAG, "null json object can not be updated in latest public data table");
        }
    }

    public void updatePrivateDataArray(JSONArray jsonArray){
        if(jsonArray!=null){
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL(DELETE_TABLE_PRIVATE_DATA);
            db.execSQL(CREATE_PRIVATE_TABLE);

            insertPrivateDataArray(jsonArray);

            Log.i(TAG, "updated private data json array successfully!");
        } else {
            Log.d(TAG, "null json array can not be updated in private data table");
        }
    }

    public void updateRefreshedDataArray(JSONArray jsonArray){
        SQLiteDatabase db = this.getWritableDatabase();
        if(jsonArray!=null){
            db.execSQL(DELETE_TABLE_PUBLIC_DATA);
            db.execSQL(CREATE_PUBLIC_DATA);

            insertPublicDataArray(jsonArray);
        }
    }

    public void updatePublicDataArray(JSONArray jsonArray){
        if(jsonArray!=null){
            JSONArray existingArray = readPublicDataValue();
            SQLiteDatabase db = this.getWritableDatabase();

            JSONObject existingObj, newObj;

            if(existingArray!=null && !(existingArray.length()<1)){
                try{
                    for(int i=0;i<existingArray.length();i++){
                        existingObj = existingArray.getJSONObject(i);
                        for(int j=0;j<jsonArray.length();j++){
                            newObj = jsonArray.getJSONObject(j);
                            if(existingObj.getString("deviceId").equals(newObj.getString("deviceId"))){
                                existingArray.put(i,newObj);
                            }
                        }
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception occurred with details : "+e.toString());
                }
                db.execSQL(DELETE_TABLE_PUBLIC_DATA);
                db.execSQL(CREATE_PUBLIC_DATA);

                insertPublicDataArray(existingArray);
            } else {
                db.execSQL(DELETE_TABLE_PUBLIC_DATA);
                db.execSQL(CREATE_PUBLIC_DATA);
            }

            Log.i(TAG, "updated public json array successfully!");
        } else {
            Log.d(TAG, "null json array can not be updated in public data table");
        }
    }

    public void deletePublicData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_TABLE_PUBLIC_DATA);
        db.execSQL(CREATE_PUBLIC_DATA);
    }

    public void deletePrivateData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_TABLE_PRIVATE_DATA);
        db.execSQL(CREATE_PRIVATE_TABLE);
    }

    public void deleteUserData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_TABLE_USER);
        db.execSQL(CREATE_USER_DATA);
    }
}
