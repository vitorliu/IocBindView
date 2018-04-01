package com.example.iocdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ioclibrary.CheckNet;
import com.example.ioclibrary.OnClick;
import com.example.ioclibrary.ViewById;
import com.example.ioclibrary.ViewUtils;

public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.tv)
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
    }
    @OnClick(R.id.tv)
    @CheckNet
    public void test(View view){
        Toast.makeText(this,"test-------",Toast.LENGTH_LONG).show();
    }
}
