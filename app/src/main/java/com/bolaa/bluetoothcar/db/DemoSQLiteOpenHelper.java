package com.bolaa.bluetoothcar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者：Administrator on 2017/10/16 10:04
 * 邮箱：xiaobo.pu@bolaa.com
 * 手机:15223197346
 * 数据库辅助类
 */

public class DemoSQLiteOpenHelper extends SQLiteOpenHelper
{
    private final static String DB_NAME="demo.db";
    private final static int DB_VERSION=1;

    public DemoSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    //数据库第一次创建的时候调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE IF NOT EXISTS stu_demo("+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+"name varchar(50),"+"age varchar(50),"+"gender varchar(50));";
        db.execSQL(sql);
    }
    //数据库版本更新的时候调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
