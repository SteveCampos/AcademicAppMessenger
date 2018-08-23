package com.consultoraestrategia.messengeracademico.groupList.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.adapter.BaseAdapter;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.Grupo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends BaseAdapter<Grupo, GroupAdapter.ViewHolder> {

    private static final String TAG = GroupAdapter.class.getSimpleName();

    public GroupAdapter(List<Grupo> items) {
        super(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), position, listener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_pic_profile)
        AppCompatTextView txtPicProfile;
        @BindView(R.id.txt_name)
        AppCompatTextView txtName;
        @BindView(R.id.txt_member_counter)
        AppCompatTextView txtMemberCounter;
        @BindView(R.id.border_bottom)
        View borderBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Grupo group, int position, final Listener<Grupo> listener) {
            Log.d(TAG, "bind: " + group.getUid());
            if (!TextUtils.isEmpty(group.getName())) txtName.setText(group.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onItemSelected(group);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) listener.onItemLongSelected(group);
                    return false;
                }
            });

            int color = R.color.md_red_500;
            switch (position) {
                case 1:
                    color = R.color.md_orange_500;
                    break;
                case 2:
                    color = R.color.md_yellow_500;
                    break;
                case 3:
                    color = R.color.md_green_500;
                    break;
                case 4:
                    color = R.color.md_blue_900;
                    break;
                case 5:
                    color = R.color.md_deep_purple_500;
                    break;
            }

            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_circle);
            if (drawable == null) return;
            DrawableCompat.setTint(drawable, ContextCompat.getColor(itemView.getContext(), color));
            txtPicProfile.setBackgroundDrawable(drawable);

            if (TextUtils.isEmpty(group.getName()) || group.getName().length() < 3) return;
            txtPicProfile.setText(group.getName().substring(0, 2));

        }
    }
}
