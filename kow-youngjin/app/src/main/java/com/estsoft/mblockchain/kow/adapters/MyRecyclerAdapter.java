package com.estsoft.mblockchain.kow.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estsoft.mblockchain.kow.R;
import com.estsoft.mblockchain.kow.vo.Document;

import java.util.List;

/**
 * Created by joeylee on 2016-11-17.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private List<Document> documentList;
//    private AdapterView.OnItemClickListener onItemClickListener;
    private Context context;



    //constructor
    public MyRecyclerAdapter(List<Document> items, Context context){

        this.documentList = items;
        this.context = context;
    }




    //generate viewHolder, paste view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.complete_row,viewGroup,false);
        return new ViewHolder(view);
    }

    //the adapter combines the data in position
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Document item = documentList.get(position);
        viewHolder.textTitle.setText(item.getTitle());
        viewHolder.img.setBackgroundResource(item.getImage());
        viewHolder.textWriter.setText(item.getWriter());
        viewHolder.itemView.setTag(item);

        viewHolder.completeRow.setOnClickListener(v ->
            Toast.makeText(context, position + " " + item.getTitle(), Toast.LENGTH_SHORT).show()
        );

    }




    @Override
    public int getItemCount() {
        return documentList.size();
    }


//    public AdapterView.OnItemClickListener getOnItemClickListener() {
//        return onItemClickListener;
//    }
//
//    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout completeRow;
        public ImageView img;
        public TextView textTitle;
        public TextView textWriter;

        public ViewHolder(View itemView){
            super(itemView);

            completeRow = (LinearLayout) itemView.findViewById(R.id.complete_row);
            img = (ImageView) itemView.findViewById(R.id.img);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            textWriter = (TextView) itemView.findViewById(R.id.textWriter);

        }

    }
}
