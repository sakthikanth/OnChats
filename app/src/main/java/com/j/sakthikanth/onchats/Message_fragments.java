package com.j.sakthikanth.onchats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Sakthikanth.J on 23-Aug-16.
 */

public class Message_fragments extends Fragment {
    public Message_fragments() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg, container, false);




        return rootView;
    }




}