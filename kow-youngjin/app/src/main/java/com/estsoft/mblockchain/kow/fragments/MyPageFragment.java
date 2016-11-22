package com.estsoft.mblockchain.kow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.estsoft.mblockchain.kow.R;

/**
 * Created by joeylee on 2016-11-16.
 */

public class MyPageFragment extends Fragment {

    ImageView backgroundImg;    // 커버사진
    ImageView profileView;  // 프로필 사진

    public static MyPageFragment newInstance() { return new MyPageFragment(); }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage, container, false);

        // Setup list
        backgroundImg = (ImageView) rootView.findViewById(R.id.header_cover_image);
        profileView = (ImageView) rootView.findViewById(R.id.user_profile_photo);
        TextView nameView = (TextView) rootView.findViewById(R.id.prof_name);
        TextView profMessage = (TextView)rootView.findViewById(R.id.prof_message);

        return rootView;

    }
}

