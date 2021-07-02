package com.example.scoutingplatform;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String TAG = "DatabaseHelper";
    //Block Table
    private static final String BLOCK_TABLE_NAME = "blocks_table";
    private static final String COL0 = "ID";
    private static final String COL1 = "BlockName";
    private static final String COL2 = "PushedCoords";
    private static final String COL3 = "PUCID";
    private static final String COL4 = "BlockID";
    private static final String COL5 = "SubblockID";

    //Production Unit Table
    private static final String PRODUCTION_UNIT_TABLE_NAME = "production_unit_table";
    private static final String PUCOL0 = "ID";
    private static final String PUCOL1 = "PUName";
    private static final String PUCOL2 = "PUID";

    //Scouting Methods Table
    private static final String METHODS_TABLE_NAME = "methods_table";
    private static final String MCOL0 = "ID";
    private static final String MCOL1 = "Description";
    private static final String MCOL2 = "AmountofCapturePoints";
    private static final String MCOL3 = "AmountofDataPoints";
    private static final String MCOL4 = "ScanBarcode";

    //PDDD's table
    private static final String PDDD_TABLE_NAME = "pddd_table";
    private static final String PDDD_COL0 = "ID";
    private static final String PDDD_COL1 = "Description";
    private static final String PDDD_COL2 = "AskforGender";
    private static final String PDDD_COL3 = "MeasurementType";
    private static final String PDDD_COL4 = "ScoutingMethods";
    private static final String PDDD_COL5 = "Phases";
    private static final String PDDD_COL6 = "PossiblePestLocation";
    private static final String PDDD_COL7 = "AskForTrap";

    //Capture Table
    private static final String CAP_TABLE_NAME = "cap_table";
    private static final String CAP_COL0 = "ID";
    private static final String CAP_COL1 = "CapturePoint";
    private static final String CAP_COL2 = "Gender";
    private static final String CAP_COL3 = "ScoutingMethod";
    private static final String CAP_COL4 = "Phase";
    private static final String CAP_COL5 = "PestLocation";
    private static final String CAP_COL6 = "Location";
    private static final String CAP_COL7 = "Timestamp";
    private static final String CAP_COL8 = "ProductionUnit";
    private static final String CAP_COL9 = "Block";
    private static final String CAP_COL10 = "SubBlock";
    private static final String CAP_COL11 = "Quantity";
    private static final String CAP_COL12 = "Severity";
    private static final String CAP_COL13 = "DataPoint";
    private static final String CAP_COL14 = "PestDescription";
    private static final String CAP_COL15 = "GUID";
    private static final String CAP_COL16 = "postKey";
    private static final String CAP_COL17 = "ImagePath";
    private static final String CAP_COL18 = "BlockID";
    private static final String CAP_COL19 = "Barcode";
    private static final String CAP_COL20 = "Trap";

    //Photos Table
    private static final String PHOTO_TABLE_NAME = "capphoto_table";
    private static final String PHOTO_COL0 = "ID";
    private static final String PHOTO_COL1 = "CapturePoint";
    private static final String PHOTO_COL2 = "photobase64";


    //Tracking Table History
    private static final String LOCH_TABLE_NAME = "loch_table";
    private static final String LHCOL0 = "ID";
    private static final String LHCOL1 = "Coordinates";
    private static final String LHCOL2 = "Date";

    //Log table for responses
    private static final String LOG_TABLE_NAME = "log_table";
    private static final String LOG0 = "ID";
    private static final String LOG1 = "status";
    private static final String LOG2 = "idcol";
    private static final String LOG3 = "keycol";
    private static final String LOG4 = "error";
    private static final String LOG5 = "code";
    private static final String LOG6 = "source";
    private static final String LOG7 = "message";


    public DatabaseHelper(@Nullable Context context) {
        super(context, BLOCK_TABLE_NAME, null, 19);
    }

    //Create Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Replace with database transactions and not one by one creations
        try {
            String createCapTable = "CREATE TABLE IF NOT EXISTS " + CAP_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CAP_COL1 + " TEXT, " +
                    CAP_COL2 + " TEXT, " +
                    CAP_COL3 + " TEXT, " +
                    CAP_COL4 + " TEXT, " +
                    CAP_COL5 + " TEXT, " +
                    CAP_COL6 + " TEXT, " +
                    CAP_COL7 + " TEXT, " +
                    CAP_COL8 + " TEXT, " +
                    CAP_COL9 + " TEXT, " +
                    CAP_COL10 + " TEXT, " +
                    CAP_COL11 + " FLOAT, " +
                    CAP_COL12 + " TEXT, " +
                    CAP_COL13 + " TEXT, " +
                    CAP_COL14 + " TEXT, " +
                    CAP_COL15 + " TEXT, " +
                    CAP_COL16 + " TEXT, " +
                    CAP_COL17 + " TEXT, " +
                    CAP_COL18 + " TEXT, " +
                    CAP_COL19 + " TEXT) ";

            String createPhotoTable = "CREATE TABLE IF NOT EXISTS " + PHOTO_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PHOTO_COL1 + " TEXT, " +
                    PHOTO_COL2 + " BLOB) ";

            String createBlocksTable = "CREATE TABLE IF NOT EXISTS " + BLOCK_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL1 + " TEXT, " +
                    COL2 + " TEXT, " +
                    COL3 + " TEXT, " +
                    COL4 + " TEXT, " +
                    COL5 + " TEXT) ";

            String createPUTable = "CREATE TABLE IF NOT EXISTS " + PRODUCTION_UNIT_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PUCOL1 + " TEXT, " +
                    PUCOL2 + " TEXT )";

            String createPDDDTable = "CREATE TABLE IF NOT EXISTS " + PDDD_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PDDD_COL1 + " TEXT, " +
                    PDDD_COL2 + " INT, " +
                    PDDD_COL3 + " INT, " +
                    PDDD_COL4 + " TEXT, " +
                    PDDD_COL5 + " TEXT, " +
                    PDDD_COL6 + " TEXT) ";

            String lochTable = "CREATE TABLE IF NOT EXISTS " + LOCH_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LHCOL1 + " TEXT, " +
                    LHCOL2 + " TEXT) ";

            String createmethodstable = "CREATE TABLE IF NOT EXISTS " + METHODS_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MCOL1 + " TEXT, " +
                    MCOL2 + " INT, " +
                    MCOL3 + " INT, " +
                    MCOL4 + " INT)";

            String createlogtable = "CREATE TABLE IF NOT EXISTS " + LOG_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LOG1 + " INT, " + LOG2 + " INT, " + LOG3 + " TEXT, " + LOG4 + " TEXT, " + LOG5 + " INT, " + LOG6 + " TEXT, " + LOG7 + " TEXT) ";

            db.execSQL(createBlocksTable);
            db.execSQL(createPUTable);
            db.execSQL(createPDDDTable);
            db.execSQL(lochTable);
            db.execSQL(createCapTable);
            db.execSQL(createmethodstable);
            db.execSQL(createlogtable);
            db.execSQL(createPhotoTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Drop If app gets updated
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Replace with database transactions and not one by one creations
        try {
            db.execSQL("DROP TABLE IF EXISTS " + PRODUCTION_UNIT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PDDD_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LOCH_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + CAP_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + METHODS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PHOTO_TABLE_NAME);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Insert Blocks
    public void addPUData(List<ProductionUnit> productionUnits) {
        //This is using Transactions to insert data.
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "INSERT INTO " + PRODUCTION_UNIT_TABLE_NAME + " (" +
                    PUCOL1 + "," +
                    PUCOL2 + ") " +
                    "VALUES (?, ?)";
            db.beginTransaction();
            Log.d(TAG, "addPUData: " + productionUnits);
            SQLiteStatement stmt = db.compileStatement(sql);
            for (int i = 0; i < productionUnits.size(); i++) {
                String name = productionUnits.get(i).getPuName();
                String rowId = String.valueOf(productionUnits.get(i).getPuID());

                stmt.bindString(1, name);
                stmt.bindString(2, rowId);
                stmt.execute();
                stmt.clearBindings();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Insert Blocks
    public void addBlockData(List<Block> blocks) {
        //This is using Transactions to insert data.
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "INSERT INTO " + BLOCK_TABLE_NAME + " (" +
                    COL1 + "," +
                    COL2 + "," +
                    COL3 + "," +
                    COL4 + "," +
                    COL5 + ") " +
                    "VALUES (?, ?, ?, ?, ?)";
            db.beginTransaction();
            Log.d(TAG, "addBlockData: " + blocks);
            SQLiteStatement stmt = db.compileStatement(sql);
            for (int i = 0; i < blocks.size(); i++) {
                String block = blocks.get(i).getBlockNo();
                String pushCo = TextUtils.isEmpty(blocks.get(i).getPushedCoords()) ? "" : blocks.get(i).getPushedCoords();
                String pucId = blocks.get(i).getPucid();
                String blockId = blocks.get(i).getBlockid();
                String rowId = String.valueOf(blocks.get(i).getRowId());

                stmt.bindString(1, block);
                stmt.bindString(2, pushCo);
                stmt.bindString(3, pucId);
                stmt.bindString(4, blockId);
                stmt.bindString(5, rowId);
                stmt.execute();
                stmt.clearBindings();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Insert Scouting Methods
    public void addScoutingMethods(List<ScoutingMethods> scoutingMethods) {
        //This is using Transactions to insert data.
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "INSERT INTO " + METHODS_TABLE_NAME + " (" + MCOL1 + "," + MCOL2 + "," + MCOL3 + "," + MCOL4 + ") VALUES (?, ?, ?, ?)";
            db.beginTransaction();

            SQLiteStatement stmt = db.compileStatement(sql);
            for (int i = 0; i < scoutingMethods.size(); i++) {
                stmt.bindString(1, scoutingMethods.get(i).getDescription());
                stmt.bindLong(2, scoutingMethods.get(i).getAmountofCapturePoints());
                stmt.bindLong(3, scoutingMethods.get(i).getAmountofDataPoints());
                stmt.bindLong(4, scoutingMethods.get(i).isForceScanField());
                stmt.execute();
                stmt.clearBindings();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Add logs
    public void addLog(List<ApiResp> resps) {
        //This is using Transactions to insert data.
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "INSERT INTO " + LOG_TABLE_NAME + " (" +
                    LOG1 + "," +
                    LOG2 + "," +
                    LOG3 + "," +
                    LOG4 + "," +
                    LOG5 + "," +
                    LOG6 + "," +
                    LOG7 + ") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            db.beginTransaction();

            SQLiteStatement stmt = db.compileStatement(sql);
            for (int i = 0; i < resps.size(); i++) {
                stmt.bindLong(1, resps.get(i).getStatus());
                stmt.bindLong(2, resps.get(i).getId());
                stmt.bindString(3, resps.get(i).getKey());
                stmt.bindString(4, resps.get(i).getError());
                stmt.bindLong(5, resps.get(i).getCode());
                stmt.bindString(6, resps.get(i).getSource());
                stmt.bindString(7, resps.get(i).getMessage());
                stmt.execute();
                stmt.clearBindings();
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //inserts PDDD's data
    public boolean addPDDDData(String description, int AskForGender, int MeasurmentType, String ScoutingMethods, String Phases, String PossiblePestLocation,int AskForTrap) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(PDDD_COL1, description);
            contentValues.put(PDDD_COL2, AskForGender);
            contentValues.put(PDDD_COL3, MeasurmentType);
            contentValues.put(PDDD_COL4, ScoutingMethods);
            contentValues.put(PDDD_COL5, Phases);
            contentValues.put(PDDD_COL6, PossiblePestLocation);
            contentValues.put(PDDD_COL7, AskForTrap);

            long result = db.insert(PDDD_TABLE_NAME, null, contentValues);

            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addCapData(String CapturePoint,
                              String Gender,
                              String ScoutingMethod,
                              String Phase,
                              String PestLocation,
                              String Location,
                              String Timestamp,
                              String ProductionUnit,
                              String Block,
                              String SubBlock,
                              float Quantity,
                              String Severity,
                              String DataPoint,
                              String PestDescription,
                              String guid,
                              String imagepath,
                              String Blockid,
                              String barcode,
                              String trap) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CAP_COL1, CapturePoint);
            contentValues.put(CAP_COL2, Gender);
            contentValues.put(CAP_COL3, ScoutingMethod);
            contentValues.put(CAP_COL4, Phase);
            contentValues.put(CAP_COL5, PestLocation);
            contentValues.put(CAP_COL6, Location);
            contentValues.put(CAP_COL7, Timestamp);
            contentValues.put(CAP_COL8, ProductionUnit);
            contentValues.put(CAP_COL9, Block);
            contentValues.put(CAP_COL10, SubBlock);
            contentValues.put(CAP_COL11, Quantity);
            contentValues.put(CAP_COL12, Severity);
            contentValues.put(CAP_COL13, DataPoint);
            contentValues.put(CAP_COL14, PestDescription);
            contentValues.put(CAP_COL15, guid);
            contentValues.put(CAP_COL17, imagepath);
            contentValues.put(CAP_COL18, Blockid);
            contentValues.put(CAP_COL19, barcode);
            contentValues.put(CAP_COL20, trap);

            long result = db.insert(CAP_TABLE_NAME, null, contentValues);

            if (result == -1) {
                db.close();
                return false;
            } else {
                db.close();

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Get Captured Data
    public Cursor getCapData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CAP_TABLE_NAME + " WHERE (" +
                CAP_COL16 + " IS NULL OR " +
                CAP_COL16 + " = \"\") AND (" +
                CAP_COL17 + " IS NULL OR " +
                CAP_COL17 + " = \"\")";
        return db.rawQuery(query, null);
    }

    //Get Captured Data images
    public Cursor getCapDataImages() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + CAP_TABLE_NAME + " WHERE (" +
                CAP_COL16 + " IS NULL OR " +
                CAP_COL16 + " = \"\") AND (" +
                CAP_COL17 + " IS NOT NULL OR " +
                CAP_COL17 + " != \"\")";
        return db.rawQuery(query, null);
    }

    //Get Capture Data for Capture Point
    public Cursor getCapDataforCP(String Cappoint, String DataPoint) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " +
                COL0 + "," +
                CAP_COL1 + "," +
                CAP_COL2 + "," +
                CAP_COL3 + "," +
                CAP_COL4 + "," +
                CAP_COL5 + "," +
                CAP_COL6 + "," +
                CAP_COL7 + "," +
                CAP_COL8 + "," +
                CAP_COL9 + "," +
                CAP_COL10 + "," +
                CAP_COL11 + "," +
                CAP_COL12 + "," +
                CAP_COL13 + "," +
                CAP_COL14 + "," +
                CAP_COL15 + "," +
                CAP_COL16 + " FROM " +
                CAP_TABLE_NAME + " WHERE " +
                CAP_COL1 + " = " + "\"" + Cappoint + "\" AND " +
                CAP_COL13 + " = " + "\"" + DataPoint + "\"";
        return db.rawQuery(query, null);
    }

    public Cursor getBlockData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + BLOCK_TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public Cursor getPUData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + PRODUCTION_UNIT_TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public Cursor getBlockID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL4 + " FROM " +
                BLOCK_TABLE_NAME + " WHERE " +
                COL1 + " = \"" + name + "\"";
        return db.rawQuery(query, null);
    }

    public List<String> getMethods() {
        List<String> list = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " +
                METHODS_TABLE_NAME + " ORDER BY " +
                MCOL1 + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Log.d(TAG, "getMethods: List = " + list.get(0));
        return list;
    }

    public List<String> getBlocks() {
        List<String> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " +
                BLOCK_TABLE_NAME + " ORDER BY " +
                COL3 + " ASC, " +
                COL1 + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getBlocksByPU(String puID) {
        List<String> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " +
                BLOCK_TABLE_NAME + " WHERE " + COL3 + " = " + puID + " ORDER BY " +
                COL3 + " ASC, " +
                COL1 + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<ProductionUnit> getProductionUnits() {
        List<ProductionUnit> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " +
                PRODUCTION_UNIT_TABLE_NAME + " ORDER BY " +
                PUCOL1 + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new ProductionUnit(cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public String getProductionUnit(String block) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT " + COL3 + " FROM " + BLOCK_TABLE_NAME + " WHERE " + COL1 + " = " + "\"" + block + "\"";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            return cursor.getString(0);
        } catch (Exception err) {
            Log.d(TAG, "getProductionUnit: " + err.getMessage());
            return "";
        }
    }

    public String getsubblock(String block) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT " + COL5 + " FROM " + BLOCK_TABLE_NAME + " WHERE " + COL1 + " = " + "\"" + block + "\"";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            return cursor.getString(0);
        } catch (Exception err) {
            Log.d(TAG, "getProductionUnit: " + err.getMessage());
            return "";
        }
    }

    public long getCapCount(String CapturePoint) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT DISTINCT " + CAP_COL13 + " FROM " + CAP_TABLE_NAME + " WHERE " + CAP_COL1 + " = " + "\"" + CapturePoint + "\"";

            Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();

            cursor.close();
            db.close();
            return count;
        } catch (Exception err) {
            Log.d(TAG, "getCapCount: " + err.getMessage());
            return 1;
        }
    }

    public String getbarcode(String CapturePoint) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT DISTINCT " + CAP_COL19 + " FROM " + CAP_TABLE_NAME + " WHERE " + CAP_COL1 + " = " + "\"" + CapturePoint + "\"";

            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            String barcode = cursor.getString(0);

            cursor.close();
            db.close();
            return barcode;
        } catch (Exception err) {
            Log.d(TAG, "getBarcode: " + err.getMessage());
            return "";
        }
    }

    public Cursor getCapMarks(String guid) {
        try {
            Log.d(TAG, "getCapMarks: " + guid);
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT DISTINCT " + CAP_COL6 + " FROM " + CAP_TABLE_NAME + " WHERE " + CAP_COL1 + " LIKE " + "\"%" + guid + "%\"";

            Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();
            Log.d(TAG, "getCapMarks: " + count);

            db.close();
            return cursor;
        } catch (Exception err) {
            Log.d(TAG, "getCapCount: " + err.getMessage());
            return null;
        }
    }

    public long getCapdpCount(String CapturePoint, String DataPoint) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT " + CAP_COL14 + " FROM " +
                    CAP_TABLE_NAME + " WHERE " +
                    CAP_COL1 + " = " + "\"" + CapturePoint + "\" AND " +
                    CAP_COL13 + " = " + "\"" + DataPoint + "\"";

            Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();

            cursor.close();
            db.close();
            return count;
        } catch (Exception err) {
            Log.d(TAG, "getCapCount: " + err.getMessage());
            return 1;
        }
    }

    public long getCapMAcount() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + CAP_TABLE_NAME + " WHERE " + CAP_COL16 + " IS NULL OR " + CAP_COL16 + " = \"\"";
            Cursor cursor = db.rawQuery(selectQuery, null);
            int count = cursor.getCount();
            cursor.close();
            db.close();
            return count;
        } catch (Exception err) {
            Log.d(TAG, "getCapCount: " + err.getMessage());
            return 1;
        }
    }

    public Cursor getCapdp(String CapturePoint, String DataPoint) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT " + CAP_COL14 + " FROM " +
                    CAP_TABLE_NAME + " WHERE " +
                    CAP_COL1 + " = " + "\"" + CapturePoint + "\" AND " +
                    CAP_COL13 + " = " + "\"" + DataPoint + "\"";
            return db.rawQuery(selectQuery, null);

        } catch (Exception err) {
            Log.d(TAG, "getCapCount: " + err.getMessage());
            return null;
        }
    }

    public List<String> getPDDDDescriptions(String scoutingmethod) {
        List<String> list = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " +
                PDDD_TABLE_NAME + " WHERE " +
                PDDD_COL4 + " LIKE \"%" + scoutingmethod + "%\"" + " ORDER BY " +
                PDDD_COL1 + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        list.add("None");
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getPDDDPhases(String descr) {
        try {
            List<String> list = new ArrayList<>();
            List<String> list2 = new ArrayList<>();

            String selectQuery = "SELECT DISTINCT " + PDDD_COL5 + " FROM " +
                    PDDD_TABLE_NAME + " WHERE " +
                    PDDD_COL1 + " = " + "\"" + descr + "\"" + " ORDER BY " +
                    PDDD_COL5 + " ASC";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));

                } while (cursor.moveToNext());
            }

            for (String s : list) {
                list2.addAll(Arrays.asList(s.split(",")));
            }

            cursor.close();
            db.close();
            return list2;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getPDDDPos(String descr) {
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();

        String selectQuery = "SELECT DISTINCT " + PDDD_COL6 + " FROM " +
                PDDD_TABLE_NAME + " WHERE " +
                PDDD_COL1 + " = " + "\"" + descr + "\"" + " ORDER BY " +
                PDDD_COL6 + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }

        for (String s : list) {
            list2.addAll(Arrays.asList(s.split(",")));
        }
        cursor.close();
        db.close();
        return list2;
    }

    public Boolean getPDDDAskTrap(String descr) {
        String selectQuery = "SELECT DISTINCT " + PDDD_COL7 + " FROM " +
                PDDD_TABLE_NAME + " WHERE " +
                PDDD_COL1 + " = " + "\"" + descr + "\"" + " ORDER BY " +
                PDDD_COL2 + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        Boolean val = cursor.getInt(0) > 0;
        Log.d(TAG, "getPDDDAskTrap: " + val);
        cursor.close();
        db.close();
        return val;
    }

    public Boolean getPDDDAskGender(String descr) {
        String selectQuery = "SELECT DISTINCT " + PDDD_COL2 + " FROM " +
                PDDD_TABLE_NAME + " WHERE " +
                PDDD_COL1 + " = " + "\"" + descr + "\"" + " ORDER BY " +
                PDDD_COL2 + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        Boolean val = cursor.getInt(0) > 0;
        Log.d(TAG, "getPDDDAskGender: " + val);
        cursor.close();
        db.close();
        return val;
    }

    public Boolean getPDDDMesurementType(String descr) {
        String selectQuery = "SELECT DISTINCT " + PDDD_COL3 + " FROM " +
                PDDD_TABLE_NAME + " WHERE " +
                PDDD_COL1 + " = " + "\"" + descr + "\"" + " ORDER BY " +
                PDDD_COL3 + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        Boolean val = cursor.getInt(0) > 0;
        Log.d(TAG, "getPDDDAskGender: " + val);
        cursor.close();
        db.close();
        return val;
    }

    public void deletePDDDData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PDDD_TABLE_NAME, null, null);
    }

    public void deleteMethods() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(METHODS_TABLE_NAME, null, null);
    }

    public void deletePUData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PRODUCTION_UNIT_TABLE_NAME, null, null);
    }

    public void deletelogdata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LOG_TABLE_NAME, null, null);
    }

    public void delentryData(ArrayList<Integer> id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String args = TextUtils.join(", ", id);

            db.execSQL(String.format("DELETE FROM " + CAP_TABLE_NAME + " WHERE " + CAP_COL0 + " IN (%s);", args));
            Log.d("DELETE", "delentryData: " + String.format("DELETE FROM " + CAP_TABLE_NAME + " WHERE " + CAP_COL0 + " IN (%s);", args));

        } catch (Exception e) {
            Log.d("execptions", e.toString());
        }
    }

    public void deleteBlockData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BLOCK_TABLE_NAME, null, null);
    }

    public void deleteHistoryData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LOCH_TABLE_NAME, null, null);
    }

    public void addLocationHistory(String DateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LHCOL2, DateTime);
        long result = db.insert(LOCH_TABLE_NAME, null, contentValues);
    }

    public boolean appendLocationHistory(String latlong, String Date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LHCOL1, latlong);
        long result = db.update(LOCH_TABLE_NAME, contentValues, LHCOL2 + " = " + "\"" + Date + "\"", null);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getLocationHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + LOCH_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public String getLocationHistorytoday(String Date) {
        try {
            String coords = "";
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT " + LHCOL1 + " FROM " + LOCH_TABLE_NAME + " WHERE " + LHCOL2 + " = " + "\"" + Date + "\"";

            Cursor loch = db.rawQuery(query, null);

            if (loch.getCount() > 0) {
                loch.moveToFirst();
                coords = loch.getString(0);
                loch.close();

            } else {
                loch.close();
                coords = "non-existant";
            }
            return coords;
        } catch (Exception err) {
            Log.d("Errors", "getLocationHistorytoday: " + err.toString());
            return "error";
        }
    }

    public Integer getRequiredDatapoints(String Description) {
        try {

            Integer required = 0;
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT " + MCOL3 + " FROM " + METHODS_TABLE_NAME + " WHERE " + MCOL1 + " = " + "\"" + Description + "\"";

            Cursor meth = db.rawQuery(query, null);

            if (meth.getCount() > 0) {
                meth.moveToFirst();
                required = meth.getInt(0);
                meth.close();

            } else {
                meth.close();
                required = 0;
            }
            return required;
        } catch (Exception err) {
            Log.d("Errors", "getLocationHistorytoday: " + err.toString());
            return 0;
        }
    }

    public Integer getRequiredCapturepoints(String Description) {
        try {

            Integer required = 0;
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT " + MCOL2 + " FROM " + METHODS_TABLE_NAME + " WHERE " + MCOL1 + " = " + "\"" + Description + "\"";

            Cursor meth = db.rawQuery(query, null);

            if (meth.getCount() > 0) {
                meth.moveToFirst();
                required = meth.getInt(0);
                meth.close();

            } else {
                meth.close();
                required = 0;
            }
            return required;
        } catch (Exception err) {
            Log.d("Errors", "getLocationHistorytoday: " + err.toString());
            return 0;
        }
    }

    public boolean getScan(String Description) {
        try {

            Integer required = 0;
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT " + MCOL4 + " FROM " + METHODS_TABLE_NAME + " WHERE " + MCOL1 + " = " + "\"" + Description + "\"";

            Cursor meth = db.rawQuery(query, null);

            if (meth.getCount() > 0) {
                meth.moveToFirst();
                required = meth.getInt(0);
                meth.close();

            } else {
                meth.close();
                required = 0;
            }
            Log.d("FORCESCAN", "database: " + required);

            if (required == 1) {

                return true;
            } else {
                return false;
            }
        } catch (Exception err) {
            Log.d("Errors", "getLocationHistorytoday: " + err.toString());
            return false;
        }
    }

    public void flagCaptured(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CAP_COL16, key);
        db.update(CAP_TABLE_NAME, cv, CAP_COL15 + " = " + "\"" + key + "\"", null);
        Log.d(TAG, "flagCaptured: Record updated! " + key);
    }

    public Cursor getlogstatData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT DISTINCT " + LOG1 + " FROM " + LOG_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getlogData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + LOG_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getlogDatabystat(String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + LOG_TABLE_NAME + " WHERE " + LOG1 + " = " + status + " ORDER BY " + LOG0 + " DESC";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}