package com.j.sakthikanth.onchats;

/**
 * Created by Sakthikanth on 8/23/2016.
 */
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.util.zip.Inflater;

public class Contact_fragment extends Fragment  {

    public  ArrayList<Names_obj> cont_lists;
    public ProgressDialog Dialog;
    public LinearLayout  contact_hold;
    public Contact_fragment() {

        //LongOperation lon= (LongOperation) new  LongOperation().execute("");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        Dialog= new ProgressDialog(getActivity());

        contact_hold=(LinearLayout)rootView.findViewById(R.id.all_contacts) ;

        Dialog.setMessage("PleaseWait...");
        //Dialog.show();
        Intent insts=new Intent(getActivity(),ChatActivity.class);

        insts.putExtra("cname","Sakthikanth");
        insts.putExtra("mobno","8870229940");
        startActivity(insts);



        return rootView;
    }

    public class LongOperation  extends AsyncTask<String, Void, Void> {


        public CheckBox cb;
     protected void onPreExecute() {
            // NOTE: You can

        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {


            cont_lists=new ArrayList<Names_obj>();



            String phoneNumber = null;
            String email = null;

            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;



            StringBuffer output = new StringBuffer();

            ContentResolver contentResolver = getActivity().getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

            // Loop for every contact in the phone


            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {



                    String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                    String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                    if (hasPhoneNumber > 0) {


                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));

                        }

                        final Names_obj mp=new Names_obj();
                        mp.set_contname(name);
                        mp.setMob_no(phoneNumber);
                        cont_lists.add(mp);
                        phoneCursor.close();


                    }


                }

            }
            return null;
        }


        protected void onPostExecute(Void unused) {


           Dialog.dismiss();

            for(int i=0;i<10;i++){
                final Names_obj nm=cont_lists.get(i);
                LayoutInflater inf=getActivity().getLayoutInflater();
                View convertView=inf.inflate(R.layout.single_contact,null);

                TextView tv=(TextView)convertView.findViewById(R.id.cont_name);
                TextView mb=(TextView)convertView.findViewById(R.id.mob_no);
                LinearLayout single_cont=(LinearLayout)convertView.findViewById(R.id.single_cont_container);
                cb=(CheckBox)convertView.findViewById(R.id.check_box);
                cb.setTag("ck_box"+i);
                final int g=i;
                single_cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                          //  Toast.makeText(getActivity(),"click"+g,Toast.LENGTH_LONG).show();
                       // cb.setChecked(true);
                       // cb.findViewWithTag("ck_box"+g).setPressed(true);
                        Intent insts=new Intent(getActivity(),ChatActivity.class);

                        insts.putExtra("cname",nm.getcontname());
                        insts.putExtra("mobno",nm.getMob_no());
                        startActivity(insts);



                    }
                });
                tv.setText(nm.getcontname());
                mb.setText(nm.getMob_no());
                contact_hold.addView(convertView);

            }




        }



    }





}