package com.j.sakthikanth.onchats;

/**
 * Created by Sakthikanth on 8/23/2016.
 */
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.util.zip.Inflater;

public class Contact_fragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public  ArrayList<Names_obj> cont_lists;
    public ProgressDialog Dialog;
    public LinearLayout  contact_hold;
    public String all_no="";
    public SimpleCursorAdapter cursorAdapter;
    public  ListView listView;
    public SQLiteDatabase db;
    public Database_funs db_funs;
    private String mSearchString = null;

    private SearchView searchView;


    public static final String[] PROJECTION ={"cname","mob_nos"};
    public static final String SELECTION = Database_funs.KEY_NAME;
    final String[] FROM = {Database_funs.TABLE_cont_lists};
    final int[] TO = {android.R.id.text1};
    private static final String[] ARGS = null;
    private static final String ORDER = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);



        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);


        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search Name Or Phone");
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.refresh_cnct){

            cursorAdapter.swapCursor(null);
            new LongOperation().execute();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        String phoneNumber = viewHolder.phoneNumber.getText().toString();
        String name = viewHolder.contactName.getText().toString();

        Intent ins=new Intent(getActivity(),ChatActivity.class);
        ins.putExtra("cname",name);
        ins.putExtra("mobno",phoneNumber);
        startActivity(ins);


      
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0, null, this);

        cursorAdapter=new IndexedListAdapter(getActivity(),
                R.layout.contact_item,
                null,
                new String[]{"cname","mob_nos"},
                new int[]{R.id.display_name,R.id.phone_number});

        setListAdapter(cursorAdapter);
        getListView().setFastScrollEnabled(true);




    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.v("txt_chng",newText);

        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;


        mSearchString = newFilter;
        try{
            getLoaderManager().restartLoader(0, null, this);

        }catch (Exception e){
            Log.v("load_mgr",e.getMessage());
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       final String[] tableColumns = new String[] {
                "_id","cname","mob_nos"};

        return new CursorLoader( getActivity(), null, tableColumns, null, null, "cname" )
        {
            @Override
            public Cursor loadInBackground()
            {
                // You better know how to get your database.
                SQLiteDatabase sqLiteDatabase=getActivity().openOrCreateDatabase("on_chats",Context.MODE_PRIVATE,null);
                // You can use any query that returns a cursor.

                String whereClause=null;

                String[] whereArgs =null;
                if(mSearchString==null){


                }else{

                    if(TextUtils.isDigitsOnly(mSearchString)){
                        whereClause="mob_nos LIKE "+"'%"+mSearchString+"%'";

                    }else{
                        whereClause="cname LIKE "+"'%"+mSearchString+"%'";

                    }

                }

                String orderBy = "cname asc";


              //  Cursor crs=sqLiteDatabase.rawQuery("select cname,mob_nos,_id,photo from cont_lists  order by cname asc ",null);
               return sqLiteDatabase.query( "cont_lists", tableColumns, whereClause, null, null, null, orderBy, null );



            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);


        //loadhosts();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        cursorAdapter.swapCursor(null);

    }

    @Override
    public boolean onClose() {
        return false;
    }

    class IndexedListAdapter extends SimpleCursorAdapter implements SectionIndexer {

        AlphabetIndexer alphaIndexer;

        public IndexedListAdapter(Context context, int layout, Cursor c,
                                  String[] from, int[] to) {
            super(context, layout, c, from, to, 0);
        }

        @Override
        public Cursor swapCursor(Cursor c) {


            if (c != null) {
                alphaIndexer = new AlphabetIndexer(c,
                        c.getColumnIndex("cname"),
                        " ABCDEFGHIJKLMNOPQRSTUVWXYZ");

            }

            return super.swapCursor(c);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                LayoutInflater inflater = getLayoutInflater(null);
                convertView = inflater.inflate(R.layout.contact_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.full_holders=(LinearLayout)convertView.findViewById(R.id.full_holder);
                viewHolder.fst_ltr_img=(TextView)convertView.findViewById(R.id.fst_letr_img);



                viewHolder.contactName = (TextView) convertView.findViewById(R.id.display_name);
               // viewHolder.phoneLabel = (TextView) convertView.findViewById(R.id.phone_label);
                viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
               // viewHolder.separator = convertView.findViewById(R.id.label_separator);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }

            return super.getView(position, convertView, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {


            super.bindView(view, context, cursor);
            ViewHolder viewHolder = (ViewHolder) view.getTag();

            String fst_ltr=cursor.getString(cursor.getColumnIndex("cname")).substring(0,1).toUpperCase();
            viewHolder.fst_ltr_img.setText(fst_ltr);
            //viewHolder.fst_ltr_img.setBackgroundColor(Color.BLUE);


        }

        @Override
        public int getPositionForSection(int section) {
            Log.v("sec_sec",section+"");
            return alphaIndexer.getPositionForSection(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            Log.v("sec_pos",position+"");
            return alphaIndexer.getSectionForPosition(position);
        }

        @Override
        public Object[] getSections() {

            return alphaIndexer == null ? null : alphaIndexer.getSections();
        }
    }



    static class ViewHolder{
        TextView contactName;
        TextView phoneLabel;
        TextView phoneNumber;
        View separator;
        TextView fst_ltr_img;
        LinearLayout full_holders;

    }


    public void cra_lod(){
        getLoaderManager().restartLoader(0, null, this);

    }

    public  class Database_funs extends SQLiteOpenHelper {

        // All Static variables
        // Database Version
        private static final int DATABASE_VERSION = 1;

        // Database Name
        private static final String DATABASE_NAME = "on_chats";

        // Contacts table name
        private static final String TABLE_CONTACTS = "contacts";
        private static final String TABLE_cont_lists = "cont_lists";
        private static final String TABLE_chats = "chats";
        private static final String TABLE_way2_dets = "way2_dets";



        // Contacts Table Columns names
        private static final String KEY_ID = "id";
        private static final String KEY_NAME = "cname";
        private static final String KEY_PH_NO = "mob_nos";

        public Database_funs(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

   }


    public class LongOperation  extends AsyncTask<String, Void, Void> implements LoaderManager.LoaderCallbacks<Cursor> {


        private boolean  succs;
        private Cursor cr;
        public String all_nm="";
        public int updt_chk=0;

        private ContentValues cvs=new ContentValues();
        protected void onPreExecute() {

            Log.v("db_funs","pre exec");

        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {


            SQLiteDatabase db=getActivity().openOrCreateDatabase("on_chats",Context.MODE_PRIVATE,null);


            // Drop older table if existed
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = getActivity().getContentResolver().query(contactsUri, null, null, null,
                    ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP+ " ASC ");


            if(contactsCursor.moveToLast()){

                do{
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact
                    Cursor dataCursor = getActivity().getContentResolver().query(dataUri, null,
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
                                    File cacheDirectory = getActivity().getBaseContext().getCacheDir();

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

                                    }

                                }

                                all_nm+=mobilePhone+",";


                            }
                        }catch (Exception e){
                        }

                   }

                }while(contactsCursor.moveToPrevious());


            }

            return null;
        }


        protected void onPostExecute(Void unused) {

            SQLiteDatabase dbs=getActivity().openOrCreateDatabase("on_chats",Context.MODE_PRIVATE,null);
            cr=dbs.rawQuery("select _id from cont_lists ",null);
            try{

                getLoaderManager().restartLoader(0, null, this);

                Toast.makeText(getActivity(),"Successfully Refreshed",Toast.LENGTH_LONG).show();

            }catch (Exception e){
                Toast.makeText(getActivity(),"Successfully Refreshed",Toast.LENGTH_LONG).show();

            }


        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            final String[] tableColumns = new String[] {
                    "_id","cname","mob_nos"};

            return new CursorLoader( getActivity(), null, tableColumns, null, null, "cname" )
            {
                @Override
                public Cursor loadInBackground()
                {
                    // You better know how to get your database.
                    SQLiteDatabase sqLiteDatabase=getActivity().openOrCreateDatabase("on_chats",Context.MODE_PRIVATE,null);
                    // You can use any query that returns a cursor.

                    String whereClause=null;


                    String orderBy = "cname asc";


                    //  Cursor crs=sqLiteDatabase.rawQuery("select cname,mob_nos,_id,photo from cont_lists  order by cname asc ",null);
                    return sqLiteDatabase.query( "cont_lists", tableColumns, whereClause, null, null, null, orderBy,null );



                }
            };

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            cursorAdapter.swapCursor(data);

            Log.v("load_fin",data.getCount()+"");

            if(data.moveToFirst()){
                while (data.moveToNext()){
                    String nnm=data.getString(data.getColumnIndex("cname"));
                    Log.v("names_l",nnm);
                }
            }

            //loadhosts();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {


            cursorAdapter.swapCursor(null);

        }

    }

}