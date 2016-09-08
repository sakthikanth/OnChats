package com.j.sakthikanth.onchats;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Otp_verfication extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verfication);

        Button btn_inp=(Button)findViewById(R.id.verify_btn);
        final EditText otp_inp=(EditText)findViewById(R.id.otp_inp);

        btn_inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp_num=otp_inp.getText().toString();
                SQLiteDatabase db=openOrCreateDatabase("on_chats",MODE_PRIVATE,null);

                try{

                    Cursor cr=db.rawQuery("select mob_no from way2_dets where otp='"+otp_num+"'",null);
                    if(cr.getCount()>0){

                        ContentValues cv=new ContentValues();
                        cv.put("user_sts",2);
                        db.update("way2_dets",cv,null,null);
                        Intent ins=new Intent(getApplicationContext(),MainPage2.class);
                        startActivity(ins);

                    }else{
                        Toast.makeText(Otp_verfication.this, "Wrong OTP", Toast.LENGTH_SHORT).show();

                    }
                }catch (Exception e){
                    Toast.makeText(Otp_verfication.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
