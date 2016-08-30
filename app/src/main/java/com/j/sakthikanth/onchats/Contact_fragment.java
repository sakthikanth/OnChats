package com.j.sakthikanth.onchats;

/**
 * Created by Sakthikanth on 8/23/2016.
 */
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
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.util.zip.Inflater;

public class Contact_fragment extends Fragment {

    public  ArrayList<Names_obj> cont_lists;
    public ProgressDialog Dialog;
    public LinearLayout  contact_hold;

    public  ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        Dialog= new ProgressDialog(getActivity());

      //  getActivity().getMenuInflater().inflate(R.menu.menu_main_page, null);

        listView=(ListView)rootView.findViewById(R.id.list_hold);
        Dialog.setMessage("Please Wait...");
        Dialog.show();

        LongOperation lon= (LongOperation) new  LongOperation().execute("");



        return rootView;
    }

    public class LongOperation  extends AsyncTask<String, Void, Void> {


        public CheckBox cb;
        public String all_name="";
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

            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
            // Loop for every contact in the phone


            if (cursor.moveToFirst() ) {

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

                        phoneNumber=phoneNumber.replace(" ","");
                        if(!all_name.contains(phoneNumber) && !phoneNumber.contains("*") && !phoneNumber.contains("#") && phoneNumber.length()>=10){
                            all_name+=phoneNumber+",";
                            final Names_obj mp=new Names_obj();
                            mp.set_contname(name);
                            mp.setMob_no(phoneNumber);
                            cont_lists.add(mp);

                        }
                        phoneCursor.close();


                    }


                }

            }
            return null;
        }


        private void loadhosts(){
            LayoutInflater inf=getActivity().getLayoutInflater();
            View convertView=inf.inflate(R.layout.fragment_contact,null);



            Load_conts lods=new Load_conts();
            Log.v("cont_list",lods.getCount()+"");
           // Toast.makeText(getActivity(),lods.getCount(),Toast.LENGTH_LONG).show();
            listView.setAdapter(lods);
        }
        protected void onPostExecute(Void unused){

            Dialog.dismiss();
            loadhosts();

        } /*{


           final String[] all_nm=new String[cont_lists.size()];
            final String[] all_mob_no=new String[cont_lists.size()];

            for(int i=0;i<cont_lists.size();i++){
                final Names_obj nms=cont_lists.get(i);
                all_nm[i]=nms.getcontname();
                all_mob_no[i]=nms.getMob_no();


            }

            for(int i=0;i<all_nm.length;i++){
                for(int n=0;n<all_nm.length;n++){
                    if(all_nm[i].trim().toLowerCase().compareTo(all_nm[n].trim().toLowerCase())<0){
                        String tmp=all_nm[i];
                        all_nm[i]=all_nm[n];

                        all_nm[n]=tmp;

                        String temp_mob=all_mob_no[i];
                        all_mob_no[i]=all_mob_no[n];
                        all_mob_no[n]=temp_mob;

                    }
                    Log.v("nums",all_nm[i]);
                }

            }





            for(int i=0;i<all_nm.length;i++){
                LayoutInflater inf=getActivity().getLayoutInflater();
                View convertView=inf.inflate(R.layout.single_contact,null);

                TextView tv=(TextView)convertView.findViewById(R.id.cont_name);
                TextView mb=(TextView)convertView.findViewById(R.id.mob_no);
                LinearLayout single_cont=(LinearLayout)convertView.findViewById(R.id.single_cont_container);

               LinearLayout cont_inr_holders=(LinearLayout)convertView.findViewById(R.id.cont_inr_holder);



                cb=(CheckBox)convertView.findViewById(R.id.check_box);
                cb.setTag("ck_box"+i);
                final int g=i;
                cont_inr_holders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent insts=new Intent(getActivity(),ChatActivity.class);

                        insts.putExtra("cname",all_nm[g]);
                        insts.putExtra("mobno",all_mob_no[g]);
                        startActivity(insts);

                    }
                });
                single_cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                          //  Toast.makeText(getActivity(),"click"+g,Toast.LENGTH_LONG).show();
                       // cb.setChecked(true);
                       // cb.findViewWithTag("ck_box"+g).setPressed(true);
                        Intent insts=new Intent(getActivity(),ChatActivity.class);

                        insts.putExtra("cname",all_nm[g]);
                        insts.putExtra("mobno",all_mob_no[g]);
                        startActivity(insts);



                    }
                });
                tv.setText(all_nm[i]);
                mb.setText(all_mob_no[i]);

               // contact_hold.addView(convertView);

            }


            Toast.makeText(getActivity(), "len "+cont_lists.size(), Toast.LENGTH_SHORT).show();

            Dialog.dismiss();





        }*/



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }


    public class Load_conts extends BaseAdapter{

        @Override
        public int getCount() {
            return cont_lists.size();
        }

        @Override
        public Object getItem(int i) {
            return cont_lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final Names_obj nm=cont_lists.get(i);
            LayoutInflater inf= LayoutInflater.from(getActivity());
            view=inf.inflate(R.layout.single_contact,viewGroup,false);

            TextView tv=(TextView)view.findViewById(R.id.cont_name);
            TextView mb=(TextView)view.findViewById(R.id.mob_no);


            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ints=new Intent(getActivity(),ChatActivity.class);
                    ints.putExtra("cname",nm.getcontname());
                    ints.putExtra("mob_no",nm.getMob_no());

                    startActivity(ints);
                }
            });
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ints=new Intent(getActivity(),ChatActivity.class);
                    ints.putExtra("cname",nm.getcontname());
                    ints.putExtra("mob_no",nm.getMob_no());

                    startActivity(ints);
                }
            });


            tv.setText(nm.getcontname());
            mb.setText(nm.getMob_no());

            return view;
        }
    }

    public class list_cls extends ListView{

        public list_cls(Context context) {
            super(context);
        }
    }
}