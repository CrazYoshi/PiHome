package com.example.marcotoni.pihome;

import java.util.ArrayList;
import java.util.TreeSet;
import android.content.Context;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<EventListItem> mData = new ArrayList<EventListItem>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private LayoutInflater mInflater;
    private Animation animation_fm;
    private Animation animation_tm;
    private ImageView ivFlip;

    private ActionMode mMode;
    private eventlist.ActionBarCallBack actionBarCallBack;

    private boolean isActionModeShowing;
    private int checkedCount = 0;

    public CustomAdapter(Context context, eventlist.ActionBarCallBack actionBarCallBack) {
        this.actionBarCallBack = actionBarCallBack;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        animation_fm = AnimationUtils.loadAnimation(context,R.anim.from_middle);
        animation_tm = AnimationUtils.loadAnimation(context,R.anim.to_middle);

        isActionModeShowing = false;
    }

    public void addItem(final EventListItem item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final EventListItem item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    public void deleteItems(){
        mData.clear();
        sectionHeader.clear();
        notifyDataSetChanged();
    }

    public void PostAction()
    {
        isActionModeShowing = false;
        mMode.finish();
        checkedCount = 0;
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public EventListItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {                      // reuse views
            if(getItemViewType(position) == TYPE_ITEM) {
                rowView = mInflater.inflate(R.layout.eventrow, null);
                ViewHolder holder = new ViewHolder();   // configure view holder
                holder.textViewTitle = (TextView) rowView.findViewById(R.id.eventTitle);
                holder.textViewDescription = (TextView) rowView.findViewById(R.id.eventDescr);
                holder.ImageViewIcon = (ImageView) rowView.findViewById(R.id.eventIcon);
                holder.ImageViewNotifiedIcon = (ImageView) rowView.findViewById(R.id.eventNotificationIcon);
                holder.ImageViewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        ivFlip = (ImageView) view;
                        ivFlip.clearAnimation();
                        ivFlip.setAnimation(animation_tm);
                        ivFlip.startAnimation(animation_tm);

                        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                EventListItem eventListItem = mData.get(Integer.parseInt(view.getTag().toString()));
                                if (animation == animation_tm) {
                                    if (!eventListItem.isChecked()) {
                                        ivFlip.setImageResource(R.drawable.checked);
                                    }
                                    ivFlip.clearAnimation();
                                    ivFlip.setAnimation(animation_fm);
                                    ivFlip.startAnimation(animation_fm);
                                } else {
                                    eventListItem.setIsChecked(!eventListItem.isChecked());
                                    setCount(eventListItem);
                                    setActionMode(view);
                                }
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        };

                        animation_tm.setAnimationListener(animationListener);
                        animation_fm.setAnimationListener(animationListener);
                    }
                });
                rowView.setTag(holder);
            }
            else {
                rowView = mInflater.inflate(R.layout.eventheader, null);
                ViewHolder holder = new ViewHolder();   // configure view holder
                holder.textViewTitle = (TextView) rowView.findViewById(R.id.textSeparator);
                rowView.setTag(holder);
            }
        }
        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        if(getItemViewType(position) == TYPE_ITEM){
            holder.textViewDescription.setText(mData.get(position).getDescription());
            holder.ImageViewIcon.setTag("" + position);
            SetEventIcon(holder, mData.get(position));
            SetNotificationIcon(holder,mData.get(position));
        }
        holder.textViewTitle.setText(mData.get(position).getTitle());
        return rowView;
    }

    public static class ViewHolder {
        public TextView textViewTitle;
        public TextView textViewDescription;
        public ImageView ImageViewIcon;
        public ImageView ImageViewNotifiedIcon;
    }

    private void setCount(final EventListItem eventListItem) {// Set selected count
        if (eventListItem.isChecked()) {
            checkedCount++;
        } else {
            if (checkedCount != 0) {
                checkedCount--;
            }
        }
    }

    private void setActionMode(View view) {// Show/Hide action mode
        if (checkedCount > 0) {
            if (!isActionModeShowing) {
                mMode = view.startActionMode(actionBarCallBack);
                isActionModeShowing = true;
            }
        }
        else {
            mMode.finish();
            isActionModeShowing = false;
        }
        notifyDataSetChanged();
    }

    private void SetEventIcon(ViewHolder holder, EventListItem item){
        if (item.isChecked()) holder.ImageViewIcon.setImageResource(R.drawable.checked);
        else{
            if (item.getType().equals("pir")){ holder.ImageViewIcon.setImageResource(R.drawable.eye); }
            else holder.ImageViewIcon.setImageResource(R.drawable.door);
        }
    }

    private void SetNotificationIcon(ViewHolder holder, EventListItem item){
        if(!item.isNotified()) holder.ImageViewNotifiedIcon.setImageResource(R.drawable.is_notified);
        else holder.ImageViewNotifiedIcon.setImageDrawable(null);
    }
}