package com.j.sakthikanth.onchats;

/**
 * Created by Sakthikanth on 8/23/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentB extends Fragment  {

    public FragmentB() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle inState) {
        View rootView = inflater.inflate(R.layout.fragment_b, container, false);

        Toast.makeText(getActivity().getApplicationContext(),"a",Toast.LENGTH_LONG).show();

        return rootView;
    }


}