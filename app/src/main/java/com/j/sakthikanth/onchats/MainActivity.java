package com.j.sakthikanth.onchats;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main_page);


            db=openOrCreateDatabase("on_chats",MODE_PRIVATE,null);
        Database_funs funs=new Database_funs(getApplicationContext());
        funs.onCreate(db);


    }



    public class LongOperation  extends AsyncTask<String, Void, Void> {


        private boolean  succs;
        private Cursor cr;
        public String all_nm="";
        private ContentValues cvs=new ContentValues();
        protected void onPreExecute() {

            Log.v("db_funs","pre exec");

        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            // Drop older table if existed
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = getContentResolver().query(contactsUri, null, null, null,
                    ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP+ " ASC ");


            if(contactsCursor.moveToLast()){

                do{
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact
                    Cursor dataCursor = getContentResolver().query(dataUri, null,
                            ContactsContract.Data.CONTACT_ID + "=" + contactId,
                            null, null);

                    String displayName="";
                    String nickName="";
                    String homePhone="";
                    String mobilePhone="";
                    String workPhone="";
                    String photoPath="";
                    byte[] photoByte=null;



                    if(dataCursor.moveToFirst() && dataCursor.getCount()>0){
                        // Getting Display Name
                        displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME ));
                        do{

                               // Getting NickNam
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
                                nickName = dataCursor.getString(dataCursor.getColumnIndex("data1"));

                            // Getting Phone numbers
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){
                                switch(dataCursor.getInt(dataCursor.getColumnIndex("data2"))){
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                                        homePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                                        mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                                        workPhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                }
                            }



                            // Getting Photo
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)){
                                photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));

                                if(photoByte != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);

                                    // Getting Caching directory
                                    File cacheDirectory = getBaseContext().getCacheDir();

                                    // Temporary file to store the contact image
                                    File tmpFile = new File(cacheDirectory.getPath() + "/wpta_"+contactId+".png");

                                    // The FileOutputStream to the temporary file
                                    try {
                                        FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                                        // Writing the bitmap to the temporary file as png file
                                        bitmap.compress(Bitmap.CompressFormat.PNG,100, fOutStream);

                                        // Flush the FileOutputStream
                                        fOutStream.flush();

                                        //Close the FileOutputStream
                                        fOutStream.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    photoPath = tmpFile.getPath();
                                }
                            }


                        }while(dataCursor.moveToNext());
                        String details = "";


                        Log.v("last_nm",displayName);

                        mobilePhone=mobilePhone.replace(" ","");
                          long c_id=contactId;
                        try{

                            boolean updted=false;
                            boolean avlbl=false;
                            Cursor cr1=db.rawQuery("select _id from cont_lists where orig_c_id='"+c_id+"'",null);
                            if(cr1.getCount()>0){

                            cr1=db.rawQuery("select _id from cont_lists where cname='"+displayName+"' and orig_c_id='"+c_id+"'",null);

                                if(cr1.getCount()==0){
                                    updted=true;
                                }

                                cr1=db.rawQuery("select _id from cont_lists where mob_nos='"+mobilePhone+"' and orig_c_id='"+c_id+"'",null);
                                if(cr1.getCount()==0){
                                    updted=true;
                                }
                                cr1=db.rawQuery("select _id from cont_lists where photo='"+photoPath+"' and orig_c_id='"+c_id+"'",null);
                                if(cr1.getCount()==0){
                                    updted=true;
                                }


                                avlbl=true;


                            }

                            if(avlbl==true && updted==false){
                               Log.v("last_nm","brk");
                                break;
                            }



                                if(!all_nm.contains(mobilePhone) && mobilePhone != null && !mobilePhone.equals("")){

                                        Log.v("c_id",mobilePhone+"s");
                                        cvs.put("mob_nos",mobilePhone);
                                        cvs.put("cname",displayName);
                                        cvs.put("photo",photoPath);
                                        cvs.put("orig_c_id",c_id);

                                        if(updted){
                                           db.update("cont_lists",cvs,"orig_c_id='"+c_id+"'",null);

                                        }else {
                                            if(avlbl==false){

                                                db.insert("cont_lists",null,cvs);

                                            }else{

                                            }

                                        }

                                        all_nm+=mobilePhone+",";



                                  //  Log.v("last_nm","avl");

                                }else{
                                 //   Log.v("last_nm","crta");

                                }



                        }catch (Exception e){
                            Log.v("db_err",e.getMessage());
                        }


                        // Concatenating various information to single string



                        // Adding id, display name, path to photo and other details to cursor
                    }
                }while(contactsCursor.moveToPrevious());


            }

            return null;
        }


        protected void onPostExecute(Void unused) {

            cr=db.rawQuery("select _id from cont_lists ",null);
            Toast.makeText(getApplicationContext(),"Successfully Refreshed",Toast.LENGTH_LONG).show();

            Intent ints=new Intent(getApplicationContext(),MainPage2.class);
            startActivity(ints);

        }

    }


    public  class Database_funs extends SQLiteOpenHelper {

        // All Static variables
        // Database Version
        private static final int DATABASE_VERSION = 1;

        // Database Name
        private static final String DATABASE_NAME = "on_chats";



        public Database_funs(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {


            try{

               // db.execSQL("drop table Messages",null);

                String crt_cnct= "CREATE TABLE IF NOT EXISTS users  ( user_id   INTEGER PRIMARY KEY AUTOINCREMENT, user_name TEXT, phone TEXT, email_id TEXT, pass_word TEXT  )";
                db.execSQL(crt_cnct);
                String way2_dets= "CREATE TABLE IF NOT EXISTS way2_dets  (  mob_no TEXT, pass_word TEXT ,otp TEXT,user_sts INTEGER)";
                db.execSQL(way2_dets);

                String cont_lists= "CREATE TABLE IF NOT EXISTS cont_lists  ( _id   INTEGER PRIMARY KEY AUTOINCREMENT, cname TEXT,  mob_nos TEXT, photo TEXT ,orig_c_id text )";
                db.execSQL(cont_lists);

                String msgs= "CREATE TABLE IF NOT EXISTS Messages  ( _id   INTEGER PRIMARY KEY AUTOINCREMENT, sen_der TEXT,  rece_iver TEXT, msg_text TEXT ,ti_me text,msg_sts INTEGER )";
                db.execSQL(msgs);


                Cursor cr=db.rawQuery("select user_id from users",null);

                Intent inm=null;
                if(cr.getCount()==0){


                    Log.v("cr1","1");
                    inm=new Intent(getApplicationContext(),Registration.class);
                    startActivity(inm);
                    Log.v("int","reg");
                }else{
                    Cursor cr3=db.rawQuery("select user_sts from way2_dets",null);
                    if(cr3.getCount()>0){

                        int sts=cr3.getInt(cr.getColumnIndex("user_sts"));
                        if(sts==2){
                            Intent inms=new Intent(getApplicationContext(),MainPage2.class);
                            startActivity(inms);
                            Log.v("int","m2");
                        }else{
                            Intent inms=new Intent(getApplicationContext(),Way2Login.class);
                            startActivity(inms);
                            Log.v("int","w21");
                        }



                    }else{
                        Intent inms=new Intent(getApplicationContext(),Way2Login.class);
                        startActivity(inms);

                        Log.v("int","w2");

                    }
                }







            }catch (Exception e){
                Log.v("c",e.getMessage());
            }
          // Drop older table if existed


            //new LongOperation().execute();




        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.v("db_funs","upgrd");

            new LongOperation().execute();


        }






    }

}
