package com.j.sakthikanth.onchats;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Sakthikanth on 9/6/2016.
 */

public class Chat_adaper_class  {
    private String msg_text,msg_time,sender_no,receiver_no,dte_ind,msg_id,msg_sts;



    public String getDte_ind() {
        return dte_ind;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public String getMsg_sts() {
        return msg_sts;
    }

    public String getMsg_text() {
        return msg_text;
    }

    public String getMsg_time() {
        return msg_time;
    }

    public String getReceiver_no() {
        return receiver_no;
    }

    public String getSender_no() {
        return sender_no;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public void setDte_ind(String dte_ind) {
        this.dte_ind = dte_ind;
    }

    public void setMsg_sts(String msg_sts) {
        this.msg_sts = msg_sts;
    }

    public void setMsg_text(String msg_text) {
        this.msg_text = msg_text;
    }

    public void setMsg_time(String msg_time) {
        this.msg_time = msg_time;
    }

    public void setReceiver_no(String receiver_no) {
        this.receiver_no = receiver_no;
    }

    public void setSender_no(String sender_no) {
        this.sender_no = sender_no;
    }

}
