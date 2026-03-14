package com.qh.tools;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class DBAdapter {
    public static final String DB_ACTION = "db_action";
    private static final String DB_NAME = "BD001.db";
    private static final int DB_VERSION = 1;
    private static DBAdapter mDBAdapter;
    private static Resources mResources;
    private static Context xContext;
    private SQLiteDatabase db;
    private DBOpenHelper dbOpenHelper;
    private boolean isOpen = false;

    public void close() {
    }

    private DBAdapter() {
    }

    public static DBAdapter init(Context context) {
        DBAdapter dBAdapter = mDBAdapter;
        if (dBAdapter != null) {
            return dBAdapter;
        }
        xContext = context;
        mResources = context.getResources();
        DBAdapter dBAdapter2 = new DBAdapter();
        mDBAdapter = dBAdapter2;
        return dBAdapter2;
    }

    public void open() throws SQLiteException {
        if (this.isOpen) {
            return;
        }
        DBOpenHelper dBOpenHelper = new DBOpenHelper(xContext, DB_NAME, null, 1);
        this.dbOpenHelper = dBOpenHelper;
        try {
            this.db = dBOpenHelper.getWritableDatabase();
        } catch (SQLiteException unused) {
            this.db = this.dbOpenHelper.getReadableDatabase();
        }
    }

    public long insert(ContentValues contentValues) {
        if (contentValues == null) {
            return -1L;
        }
        return this.db.insert(DBTable.DB_TABLE, null, contentValues);
    }

    public long insert(String str, ContentValues contentValues) {
        if (contentValues == null) {
            return -1L;
        }
        return this.db.insert(str, null, contentValues);
    }

    public long deleteOneData(String str, String str2, String[] strArr) {
        return this.db.delete(str, str2, strArr);
    }

    public long deleteAllData(String str) {
        return this.db.delete(str, null, null);
    }

    public Cursor queryAllData() {
        return this.db.query(DBTable.DB_TABLE, null, null, null, null, null, null);
    }

    public Cursor queryAllData(String str) {
        return this.db.query(str, null, null, null, null, null, null);
    }

    public Cursor queryAllData(String[] strArr) {
        return this.db.query(DBTable.DB_TABLE, strArr, null, null, null, null, null);
    }

    public Cursor queryDataByGroup(int i) {
        return this.db.query(DBLightTable.DB_TABLE, null, "lightgroup=" + i, null, null, null, null);
    }

    public Cursor queryDataByMAC(String str) {
        return this.db.query(DBLightTable.DB_TABLE, null, "Mac='" + str + "'", null, null, null, null);
    }

    public int upDataforTable(String str, ContentValues contentValues, String str2, String[] strArr) {
        return this.db.update(str, contentValues, str2, strArr);
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {
        private static final String DB_CREATE = "CREATE TABLE mygroup (_id integer primary key autoincrement, groupName varchar );";
        private static final String LIGHT_DB_CREATE = "CREATE TABLE mylight (Mac varchar primary key, LightName varchar , lightgroup integer);";

        public DBOpenHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
            super(context, str, cursorFactory, i);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(DB_CREATE);
            sQLiteDatabase.execSQL(LIGHT_DB_CREATE);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS mygroup");
            onCreate(sQLiteDatabase);
            Log.e(DBAdapter.DB_ACTION, "Upgrade");
        }
    }
}
