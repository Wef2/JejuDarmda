package com.mcl.jejudarmda.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcl.jejudarmda.R;

/**
 * Created by BK on 2016-11-27.
 */

public class PostingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_posting, container, false);

        return view;
    }
}
