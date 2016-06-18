package org.tamrah.monthview.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.tamrah.islamic.hijri.*;
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
        mCalendar = getInstance(mCalendar);
        if(sCalendar!=null)
            sCalendar = getInstance(sCalendar);
        today = getInstance(mCalendar);
    }

    private Calendar getInstance(Calendar calendar){
        if(calendar instanceof HijraCalendar)
            return HijraCalendar.getInstance();
        if(calendar instanceof IslamicCalendar)
            return IslamicCalendar.getInstance();
        if(calendar instanceof UmmAlQuraCalendar)
            return UmmAlQuraCalendar.getInstance();
        return Calendar.getInstance();
    }

    private Calendar cloneCalendar(Calendar calendar){
        if(calendar instanceof HijraCalendar)
            return (HijraCalendar)calendar.clone();
        if(calendar instanceof IslamicCalendar)
            return (IslamicCalendar)calendar.clone();
        if(calendar instanceof UmmAlQuraCalendar)
            return (UmmAlQuraCalendar)calendar.clone();
        return (Calendar)calendar.clone();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        final Calendar monthDate = cloneCalendar(today);
        monthDate.add(Calendar.MONTH, getCount()<getCenterPosition()?-(position+getCenterPosition()):(position-getCenterPosition()));

        monthGrid = new MonthGrid(mContext);
        final MonthDisplayHelper mainHelper = new MonthDisplayHelper(monthDate.get(Calendar.YEAR), monthDate.get(Calendar.MONTH), monthDate);
        monthGrid.getWeekGrid().setAdapter(weekDays);
        monthGrid.getDaysGrid().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, monthDate.get(Calendar.YEAR)+"-"+monthDate.get(Calendar.MONTH)+"-"+mainHelper.getDayAt(position), Toast.LENGTH_SHORT);
            }
        });
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
                Calendar calendar = cloneCalendar(monthDate);

                calendar.set(Calendar.DAY_OF_MONTH, mainHelper.getDayAt(position));
                if(!mainHelper.isWithinCurrentMonth(position))
                    if(mainHelper.getDayAt(position) > 15)
                        calendar.add(Calendar.MONTH, -1);
                    else
                        calendar.add(Calendar.MONTH, 1);

                return calendar;
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
                //initiate 2nd calendar
                if(position == 0 && sCalendar != null){
                    if(sCalendar instanceof HijraCalendar)
                        sCalendar = new HijraCalendar((Calendar)getItem(position));
                    else if(sCalendar instanceof IslamicCalendar)
                        sCalendar = new IslamicCalendar((Calendar)getItem(position));
                    else if(sCalendar instanceof UmmAlQuraCalendar)
                        sCalendar = new UmmAlQuraCalendar((Calendar)getItem(position));
                    else
                        sCalendar = ((Hijri)getItem(position)).toGregorianCalendar();
                }else if(sCalendar != null){
                    if(sCalendar instanceof Hijri)
                        ((Hijri)sCalendar).addWithoutComputeFields(Calendar.DATE, 1);
                    else
                        sCalendar.add(Calendar.DATE, 1);
                }

                if(convertView==null)
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_date, null);
                ((TextView)convertView.findViewById(R.id.textView)).setText(mainHelper.getDayAt(position)+"");
                if(sCalendar != null)
                    ((TextView)convertView.findViewById(R.id.textView2)).setText(sCalendar.get(Calendar.MONTH)+"/"+sCalendar.get(Calendar.DATE));

                int gHeight = monthGrid.getDaysGrid().getMeasuredHeight();//daysGrid.getMeasuredHeight();
                int cHeight = gHeight / 6;
                int gWidth = monthGrid.getDaysGrid().getMeasuredWidth();//daysGrid.getMeasuredWidth();
                int cWidth = gWidth / 7;
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(cWidth, cHeight);
                convertView.setLayoutParams(layoutParams);

                if(mainHelper.getDayAt(position) == today.get(Calendar.DATE)  &&
                        monthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        monthDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        mainHelper.isWithinCurrentMonth(position))
                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                else
                    convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));

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

    public int getCenterPosition(){return getCount()/2;}

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

            int gHeight = monthGrid.getWeekGrid().getMeasuredHeight();
            int cHeight = gHeight;
            int gWidth = monthGrid.getWeekGrid().getMeasuredWidth();
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
