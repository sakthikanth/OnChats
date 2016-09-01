package com.j.sakthikanth.onchats;

/**
 * Created by Sakthikanth on 8/23/2016.
 */
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        String phoneNumber = viewHolder.phoneNumber.getText().toString();
        String name = viewHolder.contactName.getText().toString();


        Toast.makeText(getActivity(), phoneNumber, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getLoaderManager().initLoader(0, null, this);

        cursorAdapter=new IndexedListAdapter(getActivity(),
                R.layout.list_item_contacts,
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
        return new CursorLoader( getActivity(), null, tableColumns, null, null, null )
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

                String orderBy = "_id";
                return sqLiteDatabase.query( "cont_lists", tableColumns, whereClause, null, null, null, orderBy, null );


            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);

        Log.v("txt_chng",mSearchString+"");


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
                convertView = inflater.inflate(R.layout.list_item_contacts, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.full_holders=(LinearLayout)convertView.findViewById(R.id.full_holder);
                viewHolder.contactName = (TextView) convertView.findViewById(R.id.display_name);
                viewHolder.phoneLabel = (TextView) convertView.findViewById(R.id.phone_label);
                viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
                viewHolder.separator = convertView.findViewById(R.id.label_separator);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }

            return super.getView(position, convertView, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(Database_funs.KEY_PH_NO));
            ViewHolder viewHolder = (ViewHolder) view.getTag();

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
        LinearLayout full_holders;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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


}