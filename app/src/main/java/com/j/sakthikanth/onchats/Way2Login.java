package com.j.sakthikanth.onchats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Way2Login extends AppCompatActivity {



    private String mob_no,pass_word;
    private AutoCompleteTextView mob_inp;
    private EditText pass_inp;
    private Button reg_btn;
    private TextView open_browse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way2_login);
        // Set up the login form.
        mob_inp=(AutoCompleteTextView)findViewById(R.id.way2mob_no);
       pass_inp=(EditText) findViewById(R.id.way2_pass);
        reg_btn=(Button)findViewById(R.id.save_btn);
        mob_no=mob_inp.getText().toString();
        pass_word=pass_inp.getText().toString();
        open_browse=(TextView)findViewById(R.id.signup_way2);
        open_browse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                open_browse.setTextColor(Color.BLUE);
                return false;
            }
        });
        open_browse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

             Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://site23.way2sms.com/content/index.html"));
            startActivity(browserIntent);

            }
        });
        reg_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SQLiteDatabase db = openOrCreateDatabase("on_chats",MODE_PRIVATE,null);
                   // db.execSQL("create table way2_dets(mob_no text,phone text,pass_word text)");

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("mob_no", mob_no);
                    contentValues.put("pass_word", pass_word);


                     //db.insert("way2_dets", null, contentValues);
                    Intent ints=new Intent(getApplicationContext(),MainPage.class);
                    startActivity(ints);
                   //new LongOperation().execute();
                    Log.v("db_sts","crtd");
                }catch (Exception e){
                    Log.v("db_sts",e.getMessage());
                }

            }
        });



    }



    /*public class LongOperation  extends AsyncTask<String, Void, Void> {

        // Required initialization

        // private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Registration.this);
        String data ="";
        String otpt="";



        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //Start Progress Dialog (Message)

            Dialog.setMessage("Please wait..");
            Dialog.show();



        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {


                Log.i("Send email", "");
                String[] TO = {""};
                String[] CC = {""};
            try{
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                intent.putExtra(Intent.EXTRA_TEXT, "Body of email");
                intent.setData(Uri.parse("mailto:sakthikanthj@gmail.com")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);
                Log.v("email_sts","email sent");
            }catch (Exception e){
                Log.v("email_sts",e.getMessage());

            }

            return null;
        }


        protected void onPostExecute(Void unused) {}

    }
*/

}

