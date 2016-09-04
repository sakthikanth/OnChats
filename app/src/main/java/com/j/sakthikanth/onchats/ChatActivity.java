package com.j.sakthikanth.onchats;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ChatActivity extends AppCompatActivity {

    private EditText msg_txt_inp;
    public  String sender_mob_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Intent ints=getIntent();
        String cname=ints.getStringExtra("cname");
        sender_mob_no=ints.getStringExtra("mobno");


        msg_txt_inp=(EditText)findViewById(R.id.msg_txt);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                String msg_text=msg_txt_inp.getText().toString();

                SQLiteDatabase db=openOrCreateDatabase("on_chats",MODE_PRIVATE,null);



                File folder=new File("storage/sdcard0/OnChats");
                boolean  succs=true;


                if(!folder.exists()){
                    succs=folder.mkdir();
                }

                 String NOTES="";

                SQLiteDatabase sdb=openOrCreateDatabase("on_chats",MODE_PRIVATE,null);
                Cursor crs=db.rawQuery("select _id from Messages ",null);
                int msg_cnt=crs.getCount();

                    try{

                        String url_msg= URLEncoder.encode(msg_text);




                       crs=db.rawQuery("select mob_no,pass_word from way2_dets ",null);

                        String my_mob_no=crs.getString(crs.getColumnIndex("mob_no"));
                        String mypass_word=crs.getString(crs.getColumnIndex("pass_word"));


                        ContentValues cv=new ContentValues();
                        cv.put("sen_der", my_mob_no);
                        cv.put("msg_text",msg_text);
                        cv.put("rece_iver",sender_mob_no);
                        Calendar c = Calendar.getInstance();

                        db.insert("Messages",null,cv);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = df.format(c.getTime());
                        cv.put("ti_me",formattedDate);

                        Toast.makeText(ChatActivity.this, formattedDate, Toast.LENGTH_SHORT).show();



                        final String msg_url="http://vlivetricks.com/sms/index.php?uid="+my_mob_no+"&pwd="+mypass_word+"&msg="+url_msg+"&to="+sender_mob_no;
                        final   LongOperation long_msg=new LongOperation();

                      //  long_msg.execute(msg_url);
                    }catch (Exception e){
                        Log.v("succs",e.getMessage());
                    }


                    Log.v("succs",succs+"");








            }
        });




        msg_txt_inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScrollView scroll=(ScrollView)findViewById(R.id.scroll_msgs);

                scroll.fullScroll(View.FOCUS_DOWN);


                          }
        });


        msg_txt_inp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportActionBar().setTitle(cname);
        msg_txt_inp=(EditText)findViewById(R.id.msg_txt);

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



        return super.onOptionsItemSelected(item);
    }


    public class LongOperation  extends AsyncTask<String, Void, Void> {

        // Required initialization

        // private final HttpClient Client = new DefaultHttpClient();
        public String Content="";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ChatActivity.this);
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

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( "" );
                wr.flush();

                Log.i("my_err", "ouput");
                // Get the server response
                try{
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    // StringBuilder sb = new StringBuilder();
                    String line = null;


                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        // Append server response in string
                        // sb.append(line + "\n");
                        otpt+=line;

                    }

                    // Append Server Response To Content String
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

            if(Content.hashCode()>0){
                Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_LONG).show();

            }
        }

    }

}
