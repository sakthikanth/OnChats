package com.j.sakthikanth.onchats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class Registration extends AppCompatActivity {



    private String pers_name,email_id,pass_word;
    private AutoCompleteTextView name_inp,email_inp;
    private EditText pass_inp;
    private Button reg_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQLiteDatabase db = openOrCreateDatabase("on_chats",MODE_PRIVATE,null);


        setContentView(R.layout.activity_registration);
        // Set up the login form.
        name_inp=(AutoCompleteTextView)findViewById(R.id.user_name);
        email_inp=(AutoCompleteTextView)findViewById(R.id.email);
        pass_inp=(EditText) findViewById(R.id.password);
        reg_btn=(Button)findViewById(R.id.email_sign_in_button);
        pers_name=name_inp.getText().toString();
        email_id=email_inp.getText().toString();
        pass_word=pass_inp.getText().toString();



        reg_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SQLiteDatabase db = openOrCreateDatabase("on_chats",MODE_PRIVATE,null);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("user_name", pers_name);
                    contentValues.put("email_id", email_id);
                    contentValues.put("pass_word", pass_word);


                    db.insert("users", null, contentValues);

                    Intent ints =new Intent(getApplicationContext(),Way2Login.class);
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

