package com.j.sakthikanth.onchats;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatActivity extends AppCompatActivity implements LoaderManager
.LoaderCallbacks<Cursor>{

    private EditText msg_txt_inp;
    public  String sender_mob_no;
    private RecyclerView  rcl;
    SQLiteDatabase sdb;
    public   ArrayList<Chat_adaper_class> chat_list_cont;
    private String my_mobi_no,recev_mobi_no;
    private ProgressDialog dialog;
    public  Cycle_adaper cycle_adaper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sdb=openOrCreateDatabase("on_chats",MODE_PRIVATE,null);

        Intent ints=getIntent();
        String cname=ints.getStringExtra("cname");
        sender_mob_no=ints.getStringExtra("mobno");

        rcl=(RecyclerView)findViewById(R.id.recyle_list);
        SQLiteDatabase db=openOrCreateDatabase("on_chats",MODE_PRIVATE,null);

        Cursor cs=db.rawQuery("select mob_no from way2_dets",null);
        if(cs.moveToFirst()){
            my_mobi_no=cs.getString(cs.getColumnIndex("mob_no"));
        }





        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chat_list_cont=new ArrayList<Chat_adaper_class>();

        LinearLayoutManager llm=new LinearLayoutManager(getApplicationContext());
       rcl.setLayoutManager(llm);
        llm.setStackFromEnd(true);


        cycle_adaper=new Cycle_adaper(chat_list_cont);

        rcl.setAdapter(cycle_adaper);
        rcl.setClickable(true);

       final Context context=this;


       /* dialog=new ProgressDialog(this,1);
        dialog.setMessage("Loading...");
        dialog.show();
       */// final LinearLayout all_msg_holder=(LinearLayout)findViewById(R.id.tot_msg_holder);


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

                Cursor crs=db.rawQuery("select _id from Messages ",null);
                int msg_cnt=crs.getCount();

                    try{

                        String url_msg= URLEncoder.encode(msg_text);




                       crs=db.rawQuery("select mob_no,pass_word from way2_dets ",null);

                        if(crs.moveToFirst()){
                            String my_mob_no=crs.getString(crs.getColumnIndex("mob_no"));
                            String mypass_word=crs.getString(crs.getColumnIndex("pass_word"));
                            my_mobi_no=my_mob_no;

                            ContentValues cv=new ContentValues();
                            cv.put("sen_der", my_mob_no);
                            cv.put("msg_text",msg_text+"");
                            cv.put("rece_iver",sender_mob_no);
                            cv.put("msg_sts",1);
                            Calendar c = Calendar.getInstance();




                            LinearLayoutManager lm=new LinearLayoutManager(getApplicationContext());


                            db.insert("Messages",null,cv);

                            Load_qu();



                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                            String formattedDate = df.format(c.getTime());
                            cv.put("ti_me",formattedDate);


                            msg_txt_inp.setText("");
                            final String msg_url="http://vlivetricks.com/sms/index.php?uid="+my_mob_no+"&pwd="+mypass_word+"&msg="+url_msg+"&to="+sender_mob_no;
                            final   LongOperation long_msg=new LongOperation();

                             long_msg.execute(msg_url);



                        }

                                           }catch (Exception e){
                        Log.v("succs",e.getMessage());
                    }


                    Log.v("succs",succs+"");

            }
        });




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportActionBar().setTitle(cname);
        msg_txt_inp=(EditText)findViewById(R.id.msg_txt);



        getSupportLoaderManager().initLoader(0,null, this);


    }
private void Load_qu(){
    getSupportLoaderManager().restartLoader(0,null,this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String[] tableColumns = new String[] {
                "_id","msg_text","msg_sts","ti_me"};
        return new CursorLoader( getApplicationContext(), null, null, null, null, null )
        {


            @Override
            public Cursor loadInBackground()
            {
                // You better know how to get your database.
                SQLiteDatabase sqLiteDatabase=openOrCreateDatabase("on_chats",MODE_PRIVATE,null);
                // You can use any query that returns a cursor.

                String whereClause="sen_der='"+my_mobi_no+"' and rece_iver='"+sender_mob_no+"'";


                String orderBy = "_id desc";
                Log.v("where_cls",whereClause);

                return sqLiteDatabase.query( "Messages", null, whereClause, null, null, null, null,null );



            }
        };
    }

    private ArrayList<Chat_adaper_class> build_cont_data(Cursor cursor){


        final ArrayList<Chat_adaper_class> lists=new ArrayList<Chat_adaper_class>();

        if(cursor.moveToFirst()){
            while (cursor.moveToNext()){
                final Chat_adaper_class mp=new Chat_adaper_class();
                String mt=cursor.getString(cursor.getColumnIndex("msg_text"));
                mp.setMsg_text(mt);
                lists.add(mp);
            }
        }


        return lists;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        chat_list_cont=build_cont_data(data);
        cycle_adaper=new Cycle_adaper(chat_list_cont);

        rcl.swapAdapter(cycle_adaper,true);






    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


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

            ScrollView scroll=(ScrollView)findViewById(R.id.scroll_msgs);

            scroll.fullScroll(View.FOCUS_DOWN);


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

            if(Content.hashCode()>0){

                try{
                    Cursor cr=sdb.rawQuery("select _id from Messages where sen_der='"+my_mobi_no+"' and rece_iver='"+recev_mobi_no+"'",null);

                    if(cr.moveToLast()){
                        ContentValues cv=new ContentValues();
                        cv.put("msg_sts",2);
                        sdb.update("Messages",cv,"_id="+cr.getInt(cr.getColumnIndex("_id"))+"",null);
                    }
                    Log.v("msg_sts","fin");
                }catch (Exception e){

                    Log.v("msg_sts",e.getMessage());
                }

                Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_LONG).show();

            }
        }

    }


    public   class Cycle_adaper extends RecyclerView.Adapter<Cycle_adaper.MyViewHolder>{




        private String sender_no,receiver_no;

        MyViewHolder myViewHolder;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, year, genre;
            public TextView msg_text,msg_time,dte_ind,msg_id,msg_sts;

            public MyViewHolder(View view) {
                super(view);
                msg_text=(TextView)view.findViewById(R.id.my_msg_txt);

            }
        }
        public Cycle_adaper(ArrayList<Chat_adaper_class> moviesList) {
            chat_list_cont = moviesList;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            View vi=inflater.inflate(R.layout.my_single_msg,parent,false);



            return new MyViewHolder(vi);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            Chat_adaper_class cp=chat_list_cont.get(position);
            holder.msg_text.setText(cp.getMsg_text());

            Log.v("msg_tet",cp.getMsg_text()+position+" size=");

        }

        @Override
        public int getItemCount() {
            return chat_list_cont.size();
        }
    }
}
