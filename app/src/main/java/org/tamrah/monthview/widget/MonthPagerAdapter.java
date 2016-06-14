package org.tamrah.monthview.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.tamrah.monthview.R;
import org.tamrah.monthview.widget.utils.MonthDisplayHelper;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by abdullah on 5/11/16.
 */
public class MonthPagerAdapter extends PagerAdapter {
    private Context mContext;
    private Calendar mCalendar;
    private Calendar sCalendar;

    private Calendar today;

    private MonthGrid monthGrid;

    public MonthPagerAdapter(Context context, Calendar mCalendar, Calendar sCalendar){
        this.mContext = context;
        this.mCalendar = mCalendar;
        this.sCalendar = sCalendar;
        setCalendars();
    }

    private void setCalendars(){
        mCalendar = mCalendar.getInstance();
        if(sCalendar!=null)
            sCalendar = sCalendar.getInstance();
        today = sCalendar.getInstance();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        mCalendar.add(Calendar.MONTH, getCount()<100?-(position+100):(position-100));

        monthGrid = new MonthGrid(mContext);
        final MonthDisplayHelper mainHelper = new MonthDisplayHelper(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar);
        monthGrid.getWeekGrid().setAdapter(weekDays);
        monthGrid.getDaysGrid().setAdapter(new ListAdapter() {
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
            public void registerDataSetObserver(DataSetObserver observer) {}

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) { }

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
                return position;
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

                int gHeight = monthGrid.getDaysGrid().getMeasuredHeight();//daysGrid.getMeasuredHeight();
                int cHeight = gHeight / 6;
                int gWidth = monthGrid.getDaysGrid().getMeasuredWidth();//daysGrid.getMeasuredWidth();
                int cWidth = gWidth / 7;
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(cWidth, cHeight);
                convertView.setLayoutParams(layoutParams);

                if(mainHelper.getDayAt(position) == today.getInstance().get(Calendar.DATE)  &&
                        mCalendar.get(Calendar.MONTH) == today.getInstance().get(Calendar.MONTH) &&
                        mCalendar.get(Calendar.YEAR) == today.getInstance().get(Calendar.YEAR) &&
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

        collection.addView(monthGrid);
        return monthGrid;
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
            ((TextView)convertView).setText(DateFormatSymbols.getInstance().getShortWeekdays()[position+1]);
            ((TextView)convertView).setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            ((TextView)convertView).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            int gHeight = monthGrid.getWeekGrid().getMeasuredHeight();//weekGrid.getMeasuredHeight();
            int cHeight = gHeight;
            int gWidth = monthGrid.getWeekGrid().getMeasuredWidth();//weekGrid.getMeasuredWidth();
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
    class MonthGrid extends LinearLayout{
        private GridView weekGrid;
        private GridView daysGrid;
        public MonthGrid(Context context){
            super(context);

            fromCode(context);

            weekGrid.setNumColumns(7);
            daysGrid.setNumColumns(7);
        }

        private void fromCode(Context context){
            setOrientation(VERTICAL);
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            weekGrid = new GridView(context);
            addView(weekGrid, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(20)));
            daysGrid = new GridView(context);
            addView(daysGrid, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        public GridView getDaysGrid() {
            return daysGrid;
        }

        public GridView getWeekGrid() {
            return weekGrid;
        }
    }

    // Convert DP to PX
    // Source: http://stackoverflow.com/a/8490361
    private int dpToPx(int dps) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    public void setMainCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar;
        setCalendars();
        notifyDataSetChanged();
    }

    public void setSecondCalendar(Calendar sCalendar) {
        this.sCalendar = sCalendar;
        setCalendars();
        notifyDataSetChanged();
    }
}
