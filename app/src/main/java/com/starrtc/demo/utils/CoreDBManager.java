package com.starrtc.demo.utils;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by xuas on 2015/5/11.
 */
public class CoreDBManager {
    private SQLiteDatabase coredb = null;
    private Integer dblock = 0;
    public synchronized void initCoreDB(String coreDBPath, String userid) {
        if(coredb == null) {
            File file = new File(coreDBPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            coredb = SQLiteDatabase.openOrCreateDatabase(coreDBPath+"coredb_" + userid + ".db", null);
        }
    }

    public void execSQL(String sql) throws SQLException {
        synchronized (dblock){
            try {
                coredb.execSQL(sql);
            }catch (SQLException e){
                throw e;
            }
        }
    }

    public void execSQL(String sql, Object[] bindArgs) throws SQLException {
        synchronized (dblock){
            try {
                coredb.execSQL(sql,bindArgs);
            }catch (SQLException e){
                throw e;
            }
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            cursor = coredb.rawQuery(sql, selectionArgs);
            //如果table不存在，cursor有可能返回null，也有可能返回非null，但调用moveToNext或获取count时会抛出表不存在的错误
            //这里使用下面的方法判断cursor是否有效
            if(cursor.getCount() <= 0){
                cursor = null;
            }
        }catch (Exception e){
            cursor = null;
        }


        return cursor;
    }

    public void close(){
        synchronized (dblock){
            if(coredb != null) {
                coredb.close();
                coredb = null;
            }
        }
    }

}
