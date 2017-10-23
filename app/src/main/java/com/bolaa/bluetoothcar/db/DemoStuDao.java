package com.bolaa.bluetoothcar.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 作者：Administrator on 2017/10/16 10:26
 * 邮箱：xiaobo.pu@bolaa.com
 * 手机:15223197346
 */

public class DemoStuDao
{
    private static DemoStuDao demoStuDao;
    private static DemoSQLiteOpenHelper demoSQLiteOpenHelper;
    private final static String TAG=DemoStuDao.class.getSimpleName();
    private SQLiteDatabase stuDataBase;
    public static DemoStuDao getInstance()
    {
        if (demoStuDao==null){
            demoStuDao = new DemoStuDao();
        }
        return demoStuDao;
    }
    private DemoStuDao(){
        if (demoSQLiteOpenHelper==null){
            demoSQLiteOpenHelper = new DemoSQLiteOpenHelper(DemoApplication.getInstance());
        }
    }
    //添加方法
    public void addData(){
        stuDataBase = demoSQLiteOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("name","张三");
        values.put("age","18");
        values.put("gender","男");
        stuDataBase.insert("stu_demo",null,values);
        Log.e(TAG,"insert>>"+"插入数据");
        //关闭数据库
        stuDataBase.close();
    }
    //删除方法
    //修改方法
    //查询方法
}
