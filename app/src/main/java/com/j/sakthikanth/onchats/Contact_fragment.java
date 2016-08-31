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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
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

public class Contact_fragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public  ArrayList<Names_obj> cont_lists;
    public ProgressDialog Dialog;
    public LinearLayout  contact_hold;
    public String all_no="";
    public SimpleCursorAdapter cursorAdapter;
    public  ListView listView;
    @SuppressLint("InlinedApi")
    private static String DISPLAY_NAME_COMPAT = Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
            ContactsContract.Contacts.DISPLAY_NAME;



    private static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
            ContactsContract.Contacts._ID,
            DISPLAY_NAME_COMPAT,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);




        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ContactsListFragment.ViewHolder viewHolder = (ContactsListFragment.ViewHolder) v.getTag();
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
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{R.id.display_name});

        setListAdapter(cursorAdapter);
        getListView().setFastScrollEnabled(true);




    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        Uri baseUri= ContactsContract.Contacts.CONTENT_URI;


        String selection = "((" + DISPLAY_NAME_COMPAT + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + ") AND ("
                + DISPLAY_NAME_COMPAT + " != '' ))";

        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        CursorLoader crl= new CursorLoader(getActivity(), baseUri, CONTACTS_SUMMARY_PROJECTION, selection, null, sortOrder);



        return crl;
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
                        c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME),
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
                viewHolder.phoneNumberLookupTask.cancel(true);
            }

            return super.getView(position, convertView, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.phoneNumberLookupTask = new PhoneNumberLookupTask(view);
            viewHolder.phoneNumberLookupTask.execute(contactId);
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
        PhoneNumberLookupTask phoneNumberLookupTask;
    }


    private class PhoneNumberLookupTask extends AsyncTask<Long, Void, Void> {
        final WeakReference<View> mViewReference;

        String mPhoneNumber;
        String mPhoneLabel;

        public PhoneNumberLookupTask(View view){
            mViewReference = new WeakReference<>(view);
        }

        @Override
        protected Void doInBackground(Long... ids) {
            String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.LABEL};
            long contactId = ids[0];

            final Cursor phoneCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);

            if(phoneCursor != null && phoneCursor.moveToFirst() && phoneCursor.getCount() == 1) {
                final int contactNumberColumnIndex 	= phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);


                mPhoneNumber = phoneCursor.getString(contactNumberColumnIndex);
                Log.v("mob_no",mPhoneNumber);

                int type = phoneCursor.getInt(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
                mPhoneLabel = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                mPhoneLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, mPhoneLabel).toString();
                phoneCursor.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            View view = mViewReference.get();
            if (view != null ){
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (mPhoneNumber != null){

                    String t_no=mPhoneNumber.replace(" ","");


                    viewHolder.phoneNumber.setText(mPhoneNumber);
                    viewHolder.phoneLabel.setText(mPhoneLabel);
                    viewHolder.phoneLabel.setVisibility(View.VISIBLE);
                    viewHolder.separator.setVisibility(View.VISIBLE);

                    if(!all_no.contains(t_no)){
                        all_no+=t_no+",";
                    }else{
                        viewHolder.full_holders.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    viewHolder.phoneNumber.setText(getString(R.string.label_multiple_numbers));
                    viewHolder.phoneLabel.setVisibility(View.GONE);
                    viewHolder.separator.setVisibility(View.GONE);
                }

            }
        }
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }





}