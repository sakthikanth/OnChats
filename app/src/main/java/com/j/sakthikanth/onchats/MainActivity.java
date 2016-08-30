package com.j.sakthikanth.onchats;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        try{
            SQLiteDatabase db = openOrCreateDatabase("on_chats",MODE_PRIVATE,null);
            db.execSQL("create table contacts (user_id integer primary key, user_name text,phone text,email_id text, pass_word text)");
            db.execSQL("create table way2_dets(mob_no text,phone text,pass_word text)");



           Intent inm=new Intent(this,Registration.class);
            startActivity(inm);
        }catch (Exception e){
            SQLiteDatabase db = openOrCreateDatabase("on_chats",MODE_PRIVATE,null);

            Intent inm=new Intent(this,MainPage2.class);



            Cursor cr1=db.rawQuery("select user_name from contacts",null);
            if(cr1.getCount()==0){

                inm=new Intent(this,Registration.class);
                startActivity(inm);

            }
            Cursor cr2=db.rawQuery("select mob_no,pass_word from way2_dets",null);
            if(cr2.getCount()==0){
                inm=new Intent(this,Way2Login.class);
            }

            startActivity(inm);

        }


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
