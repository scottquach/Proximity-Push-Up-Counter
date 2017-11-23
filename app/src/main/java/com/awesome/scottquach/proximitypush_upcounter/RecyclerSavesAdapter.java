package com.awesome.scottquach.proximitypush_upcounter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Scott Quach on 9/3/2017.
 */

public class RecyclerSavesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int SUCCESS = 0;
    private final int FAILURE = 1;

    Context context;
    SessionEntity[] savedData;

    public RecyclerSavesAdapter(Context context, SessionEntity[] savedData) {
        this.context = context;
        this.savedData = savedData;
    }

    public void resetData() {
        if (savedData != null) savedData = new SessionEntity[]{};
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SUCCESS:
                View view = LayoutInflater.from(context).inflate(R.layout.recycler_row_saves_success, parent, false);
                return new ViewHolderSuccess(view);
            case FAILURE:
                View view1 = LayoutInflater.from(context).inflate(R.layout.recycler_row_saves_failure, parent, false);
                return new ViewHolderFailure(view1);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_row_saves_success, parent, false);
        return new ViewHolderSuccess(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case SUCCESS:
                ViewHolderSuccess successHolder = (ViewHolderSuccess) holder;
                successHolder.dateView.setText(savedData[position].date);
                successHolder.pushUpsView.setText(String.valueOf(savedData[position].numberOfPushups));
                break;

            case FAILURE:
                ViewHolderFailure failureHolder = (ViewHolderFailure) holder;
                failureHolder.dateView.setText(savedData[position].date);
                failureHolder.pushUpsView.setText(String.valueOf(savedData[position].numberOfPushups));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return savedData.length;
    }

    @Override
    public int getItemViewType(int position) {
        return savedData[position].isGoalReached ? SUCCESS : FAILURE;
    }

    public class ViewHolderSuccess extends RecyclerView.ViewHolder {

        public TextView dateView;
        public TextView pushUpsView;

        public ViewHolderSuccess(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.text_date);
            pushUpsView = (TextView) itemView.findViewById(R.id.text_push_ups);
        }
    }

    public class ViewHolderFailure extends RecyclerView.ViewHolder {

        public TextView dateView;
        public TextView pushUpsView;

        public ViewHolderFailure(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.text_date);
            pushUpsView = (TextView) itemView.findViewById(R.id.text_push_ups);
        }
    }
}
