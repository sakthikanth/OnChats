package com.j.sakthikanth.onchats;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class ChatActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private EditText msg_txt_inp;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        msg_txt_inp=(EditText)findViewById(R.id.msg_txt);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        msg_txt_inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScrollView scroll=(ScrollView)findViewById(R.id.scroll_msgs);

                scroll.fullScroll(View.FOCUS_DOWN);


                          }
        });



        Intent ints=getIntent();
        String cname=ints.getStringExtra("cname");
        String mob_no=ints.getStringExtra("mobno");
        getSupportActionBar().setTitle(cname);

     //   msg_txt_inp.setFocusableInTouchMode(true);

        msg_txt_inp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                ScrollView scroll=(ScrollView)findViewById(R.id.scroll_msgs);

                scroll.fullScroll(View.FOCUS_DOWN);

            }
        });
        msg_txt_inp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                ScrollView scroll=(ScrollView)findViewById(R.id.scroll_msgs);

                scroll.fullScroll(View.FOCUS_DOWN);


                return false;
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
