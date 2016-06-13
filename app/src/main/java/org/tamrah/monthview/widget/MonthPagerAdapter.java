package org.tamrah.monthview.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.tamrah.monthview.R;
import org.tamrah.monthview.widget.utils.MonthDisplayHelper;

import java.util.Calendar;

/**
 * Created by abdullah on 5/11/16.
 */
public class MonthPagerAdapter extends PagerAdapter {
    private Context mContext;
    private Calendar mCalendar;
    private Calendar sCalendar;
    public MonthPagerAdapter(Context context, Calendar mCalendar, Calendar sCalendar){
        this.mContext = context;
        this.mCalendar = mCalendar;
        this.sCalendar = sCalendar;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.MONTH, getCount()<100?-(position+100):(position-100));
        /*MonthGrid mg = new MonthGrid(mContext, mCalendar, sCalendar);
        collection.addView(mg);
        return mg;*/
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.view_month, collection, false);
        final MonthDisplayHelper mainHelper = new MonthDisplayHelper(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar);
        GridView wGrid = (GridView)layout.findViewById(R.id.weekGV);
        wGrid.setNumColumns(7);
        wGrid.setAdapter(weekDays);
        final GridView mGrid = (GridView)layout.findViewById(R.id.monthGV);
        mGrid.setNumColumns(7);
        mGrid.setAdapter(new ListAdapter() {
            final int IN_MONTH = 1;
            final int OTHER_MONTH = -1;
            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getCount() {
                // 7 days, 6 weeks.
                return 7*6;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null)
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_date, null);
                ((TextView)convertView.findViewById(R.id.textView)).setText(mainHelper.getDayAt(position)+"");

                int gHeight = mGrid.getMeasuredHeight();
                int cHeight = gHeight / 6;
                int gWidth = mGrid.getMeasuredWidth();
                int cWidth = gWidth / 7;
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(cWidth, cHeight);
                convertView.setLayoutParams(layoutParams);

                if(mainHelper.getDayAt(position) == Calendar.getInstance().get(Calendar.DATE)  &&
                        mainHelper.isWithinCurrentMonth(position))
                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));

                return convertView;
            }

            @Override
            public int getItemViewType(int position) {
                return mainHelper.isWithinCurrentMonth(position)?IN_MONTH:OTHER_MONTH;
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });

        collection.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return 201;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    private ListAdapter weekDays = new ListAdapter() {
        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = new TextView(mContext);
            ((TextView)convertView).setText(position + "");
            ((TextView)convertView).setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            ((TextView)convertView).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            int gHeight = mGrid.getMeasuredHeight();
            int cHeight = gHeight / 6;
            int gWidth = mGrid.getMeasuredWidth();
            int cWidth = gWidth / 7;
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(cWidth, cHeight);
            convertView.setLayoutParams(layoutParams);

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    };
}
