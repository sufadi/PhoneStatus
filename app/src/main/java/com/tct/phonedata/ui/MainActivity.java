package com.tct.phonedata.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tct.phonedata.R;
import com.tct.phonedata.utils.MyConstant;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initValues();
        initListenser();
    }

    private void initViews() {
        btn_start = (Button) findViewById(R.id.btn_start);
    }

    private void initValues() {

    }

    private void initListenser() {
        btn_start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                Log.d(MyConstant.TAG, "btn_start click");
                break;
        }
    }
}
