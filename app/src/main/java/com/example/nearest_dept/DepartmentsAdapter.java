package com.example.nearest_dept;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;


public class DepartmentsAdapter extends RecyclerView.Adapter<DepartmentsAdapter.ViewHolder> {


    List<Departments_Model> listItems;
    Context mContext;

    public DepartmentsAdapter(List<Departments_Model> listItems, Context context) {
        this.listItems = listItems;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list_item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Departments_Model departmentsModel = listItems.get(position);

            holder.location.setText(departmentsModel.getDistance());
Picasso.with(mContext).load(departmentsModel.getImage()).into(holder.productImage);

holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,Papers_List.class);
                i.putExtra("name",departmentsModel.getDistance());
                i.putExtra("paper_url",departmentsModel.getPaper_url());
                mContext.startActivity(i);
            }
        }
);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView location;
        RelativeLayout item;

        public ViewHolder(View itemView) {
            super(itemView);


            productImage = itemView.findViewById(R.id.product_image);
             location=itemView.findViewById(R.id.location);
             item = itemView.findViewById(R.id.layout);
        }
    }
}
