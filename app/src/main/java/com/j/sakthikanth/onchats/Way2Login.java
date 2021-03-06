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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

public class Way2Login extends AppCompatActivity {



    private String mob_no,pass_word;
    private AutoCompleteTextView mob_inp;
    private EditText pass_inp;
    private Button reg_btn;
    private TextView open_browse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQLiteDatabase db = openOrCreateDatabase("on_chats",MODE_PRIVATE,null);

        Cursor cr=db.rawQuery("select user_sts from way2_dets",null);
        if(cr.getCount()>0){

            int sts=cr.getInt(cr.getColumnIndex("user_sts"));
            if(sts==2){
                Intent inm=new Intent(this,MainPage2.class);
                 startActivity(inm);
            }



        }
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

                    mob_no=mob_inp.getText().toString();
                    pass_word=pass_inp.getText().toString();
                       final Random rand = new Random();
                       int rand_num = rand.nextInt(9999) + 1111;

                    SQLiteDatabase db = openOrCreateDatabase("on_chats",MODE_PRIVATE,null);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("mob_no", mob_no);
                    contentValues.put("pass_word", pass_word);
                    contentValues.put("otp",rand_num);
                       contentValues.put("user_sts",1);

                       String otp_msg="Your Verification code for activation is "+rand_num;
                       otp_msg= URLEncoder.encode(otp_msg);


                          db.insert("way2_dets", null, contentValues);

                       final String msg_url="http://vlivetricks.com/sms/index.php?uid="+mob_no+"&pwd="+pass_word+"&msg="+otp_msg+"&to="+mob_no;

                       new LongOperation().execute(msg_url);

                }catch (Exception e){
                    Log.v("db_sts",e.getMessage());
                }

            }
        });



    }



    public class LongOperation  extends AsyncTask<String, Void, Void> {

        // Required initialization

        // private final HttpClient Client = new DefaultHttpClient();
        public String Content="";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Way2Login.this);
        String data ="";
        String otpt="";



        protected void onPreExecute() {
            // NOTE: You can call UI Element here.
            Dialog.setMessage("Sending...");
            Dialog.show();

        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            BufferedReader reader=null;

            // Send data
            try
            {
                Log.i("my_err","goto");
                // Defined URL  where to send data
                URL url = new URL(urls[0]);

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( "" );
                wr.flush();

                try{
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    // StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null)
                    {
                        otpt+=line;

                    }
                    Content = otpt;
                    Log.i("my_err","output="+otpt);
                }catch (Exception e){
                    Log.i("my_err","error="+e.getMessage());
                }



            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }

            /*****************************************************/
            return null;
        }


        protected void onPostExecute(Void unused) {


            Dialog.dismiss();

            if(Content.contains("SmS Sent Successfully")){

                Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_LONG).show();
                Intent ints=new Intent(getApplicationContext(),Otp_verfication.class);
                startActivity(ints);

            }else{
                Toast.makeText(getApplicationContext(),"Failed ! Try Again...",Toast.LENGTH_LONG).show();

            }
        }

    }


}

