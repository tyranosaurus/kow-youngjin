package com.estsoft.mblockchain.kow.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.estsoft.mblockchain.kow.MainActivity;
import com.estsoft.mblockchain.kow.R;
import com.estsoft.mblockchain.kow.adapters.DividerItemDecoration;
import com.estsoft.mblockchain.kow.adapters.MyRecyclerAdapter;
import com.estsoft.mblockchain.kow.vo.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joeylee on 2016-11-16.
 */

public class CompleteFragment extends Fragment {

    private RecyclerView recyclerView;

    public static CompleteFragment newInstance() { return new CompleteFragment(); }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_complete, container, false);

        // Setup list

        List<Document> documentList = new ArrayList<Document>();

        for (int i =0; i<20; i ++){
            Document document = new Document();
            document.setTitle("TITLE"+i);
            document.setWriter("WRITER"+i);
            document.setImage(R.mipmap.ic_launcher);
            documentList.add(document);
        }
        MyRecyclerAdapter myAdapter = new MyRecyclerAdapter(documentList, getContext());

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        recyclerView.setAdapter(myAdapter);
        setRecyclerViewLayoutManager(recyclerView);

        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return rootView;

    }


    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }



}

