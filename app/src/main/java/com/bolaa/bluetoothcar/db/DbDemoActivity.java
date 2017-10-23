package com.bolaa.bluetoothcar.db;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bolaa.bluetoothcar.R;

/**
 * 作者：Administrator on 2017/10/16 11:11
 * 邮箱：xiaobo.pu@bolaa.com
 * 手机:15223197346
 */

public class DbDemoActivity extends Activity implements View.OnClickListener
{
    private TextView addTextView;
    private TextView deleteTextView;
    private TextView modifyTextView;
    private TextView queryTextView;
    private DemoStuDao demoStuDao;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_db_demo);
        addTextView = (TextView) findViewById(R.id.add_tv);
        deleteTextView = (TextView) findViewById(R.id.delete_tv);
        modifyTextView = (TextView) findViewById(R.id.modify_tv);
        queryTextView = (TextView) findViewById(R.id.query_tv);
        addTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);
        modifyTextView.setOnClickListener(this);
        queryTextView.setOnClickListener(this);
        demoStuDao = DemoStuDao.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_tv:
                demoStuDao.addData();
                break;
            case R.id.delete_tv:
                break;
            case R.id.modify_tv:
                break;
            case R.id.query_tv:
                break;
        }
    }
}
