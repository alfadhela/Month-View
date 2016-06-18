package org.tamrah.islamic.hijri;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;


/**
 * @author abdullah alfadhel
 * @version 0.0.3b
 **/

public class UmmAlQuraCalendar extends Calendar implements Hijri {
    // Proclaim serialization
    private static final long serialVersionUID = 857982800223956724L;

    public static UmmAlQuraCalendar getInstance(){
        return new UmmAlQuraCalendar(Calendar.getInstance());
    }

    //
    public static UmmAlQuraCalendar getInstance(Locale locale){
        return new UmmAlQuraCalendar(Calendar.getInstance(locale));
    }

    public UmmAlQuraCalendar(Calendar calendar){
        int[] date = UmmALQura.gregorianToHijri(calendar.get(YEAR), calendar.get(MONTH)+1, calendar.get(DATE));
        fields[YEAR] = date[0];
        fields[MONTH] = date[1];
        fields[DAY_OF_MONTH] = date[2];
        fields[DAY_OF_WEEK] = date[3];
        //Set time
        for (int i = AM_PM; i < FIELD_COUNT; i++) {
            fields[i] = calendar.get(i);
        }
    }

    public UmmAlQuraCalendar(int year, int month, int day) {
        if(year < getMinimum(YEAR) || year > getMaximum(YEAR) ||
                month < getMinimum(MONTH) || month > getMaximum(MONTH) ||
                day < getMinimum(DATE) || day > getMaximum(DATE)){
            throw new IllegalArgumentException();
        }
        fields[YEAR] = year;
        fields[MONTH] = month;
        set(DAY_OF_MONTH, day);
    }

    @Override
    public Calendar toGregorianCalendar(){
        Calendar calendar = Calendar.getInstance();
        int[] date = UmmALQura.hijriToGregorian(internalGet(YEAR), internalGet(MONTH), internalGet(DATE));

        calendar.set(YEAR, date[0]);
        calendar.set(MONTH, date[1]-1);
        calendar.set(DAY_OF_MONTH, date[2]);
        //Set time
        for (int i = AM_PM; i < FIELD_COUNT; i++) {
            calendar.set(i, fields[i]);
        }

        return calendar;
    }

    @Override
    protected void computeTime() {

    }

    @Override
    protected void computeFields() {
        fields[DAY_OF_WEEK] = UmmALQura.getDayOfWeek(internalGet(YEAR), internalGet(MONTH), internalGet(DATE));
        fields[WEEK_OF_MONTH] = internalGet(DATE)/7 + (internalGet(DATE)%7>0?1:0);
        fields[DAY_OF_YEAR] = UmmALQura.getDayOfYear(internalGet(YEAR), internalGet(MONTH), internalGet(DATE));
        fields[WEEK_OF_YEAR] = internalGet(DAY_OF_YEAR)/7 + (internalGet(DAY_OF_YEAR)%7>0?1:0);
    }

    @Override
    public void set(int field, int value) {
        //Check value
        if(value > getMaximum(field) || value < getMinimum(field)){
            throw new IllegalArgumentException();
        }
        fields[field] = value;
        if(field == YEAR || field == MONTH || field == DATE)
            computeFields();
    }

    @Override
    public void setWithoutComputeFields(int field, int value) {
        //Check value
        if(value > getMaximum(field) || value < getMinimum(field)){
            throw new IllegalArgumentException();
        }
        fields[field] = value;
    }

    @Override
    public void add(int field, int amount) {
        // If amount == 0, do nothing even the given field is out of
        if (amount == 0) {
            return;   // Do nothing!
        }

        if (field < 0 || field >= ZONE_OFFSET) {
            throw new IllegalArgumentException();
        }

        switch (field) {
            case YEAR:
                if(internalGet(field) + amount > getMaximum(field) || internalGet(field) + amount < getMinimum(field))
                    throw new IllegalArgumentException();
                set(field, fields[field] + amount);
                break;
            case MONTH:
                int month = internalGet(MONTH) + amount;
                int year = (month-1) / getMaximum(MONTH);
                if(amount > 0){
                    //PLUS
                    fields[YEAR] += year;
                    if(month % getMaximum(MONTH) == 0)
                        set(MONTH, DHU_AL_HIJJAH);
                    else
                        set(MONTH, month % getMaximum(MONTH));
                }else{
                    //MINUS
                    if(month > 0){
                        set(MONTH, month);
                    }else{
                        year--;
                        fields[YEAR] += year;
                        set(MONTH, getMaximum(MONTH) + (month % getMaximum(MONTH)));
                    }
                }
                if(internalGet(DATE) == 30 && UmmALQura.getMonthLength(internalGet(YEAR), internalGet(MONTH)) != 30)
                    set(DATE, 29);
                break;
            case DAY_OF_MONTH:
                Calendar calendarDAY_OF_MONTH = toGregorianCalendar();
                calendarDAY_OF_MONTH.add(DAY_OF_MONTH, amount);
                int[] date = UmmALQura.gregorianToHijri(calendarDAY_OF_MONTH.get(YEAR), calendarDAY_OF_MONTH.get(MONTH)+1, calendarDAY_OF_MONTH.get(DATE));
                set(YEAR, date[0]);
                set(MONTH, date[1]);
                set(DAY_OF_MONTH, date[2]);
                set(DAY_OF_WEEK, date[3]);
                break;

            default:
                break;
        }
    }

    @Override
    public void addWithoutComputeFields(int field, int amount) {
        // TODO Auto-generated method stub
        // If amount == 0, do nothing even the given field is out of
        if (amount == 0) {
            return;   // Do nothing!
        }

        if (field < 0 || field >= ZONE_OFFSET) {
            throw new IllegalArgumentException();
        }

        switch (field) {
            case YEAR:
                if(internalGet(field) + amount > getMaximum(field) || internalGet(field) + amount < getMinimum(field))
                    throw new IllegalArgumentException();
                setWithoutComputeFields(field, fields[field] + amount);
                break;
            case MONTH:
                int month = internalGet(MONTH) + amount;
                int year = (month-1) / getMaximum(MONTH);
                if(amount > 0){
                    //PLUS
                    fields[YEAR] += year;
                    if(month % getMaximum(MONTH) == 0)
                        setWithoutComputeFields(MONTH, DHU_AL_HIJJAH);
                    else
                        setWithoutComputeFields(MONTH, month % getMaximum(MONTH));
                }else{
                    //MINUS
                    if(month > 0){
                        setWithoutComputeFields(MONTH, month);
                    }else{
                        year--;
                        fields[YEAR] += year;
                        setWithoutComputeFields(MONTH, getMaximum(MONTH) + (month % getMaximum(MONTH)));
                    }
                }
                if(internalGet(DATE) == 30 && UmmALQura.getMonthLength(internalGet(YEAR), internalGet(MONTH)) != 30)
                    setWithoutComputeFields(DATE, 29);
                break;
            case DAY_OF_MONTH:
                Calendar calendarDAY_OF_MONTH = toGregorianCalendar();
                calendarDAY_OF_MONTH.add(DAY_OF_MONTH, amount);
                int[] date = UmmALQura.gregorianToHijri(calendarDAY_OF_MONTH.get(YEAR), calendarDAY_OF_MONTH.get(MONTH)+1, calendarDAY_OF_MONTH.get(DATE));
                setWithoutComputeFields(YEAR, date[0]);
                setWithoutComputeFields(MONTH, date[1]);
                setWithoutComputeFields(DAY_OF_MONTH, date[2]);
                //System.out.println("DAY_OF_WEEK: " + date[3] + " OR " +(date[3]%7));
                setWithoutComputeFields(DAY_OF_WEEK, (date[3]+1));
                break;

            default:
                break;
        }
    }

    @Override
    public void roll(int field, boolean up) {

    }

    @Override
    public int getMinimum(int field) {
        int ret = 0;
        switch (field) {
            case DAY_OF_MONTH:
            case DAY_OF_WEEK:
            case MONTH:
                ret = 1;
                break;
            case YEAR:
                ret = 1300;
                break;
        }
        return ret;
    }

    @Override
    public int getMaximum(int field) {
        int ret = 0;
        switch (field) {
            case DAY_OF_MONTH:
                ret = 30;
                break;
            case DAY_OF_WEEK:
                ret = 7;
            case MONTH:
                ret = 12;
                break;
            case YEAR:
                ret = 1600;
                break;
//		case ERA:
//			ret = AH;
//			break;
        }
        return ret;
    }

    @Override
    public int getActualMaximum(int field) {
        int ret = 0;
        switch (field) {
            case DAY_OF_MONTH:
                ret = UmmALQura.getMonthLength(internalGet(YEAR), internalGet(MONTH));
                break;
            case DAY_OF_WEEK:
                ret = 7;
            case MONTH:
                ret = 12;
                break;
            case YEAR:
                ret = 1600;
                break;
        }
        return ret;
    }

    @Override
    public int getGreatestMinimum(int field) {
        return 0;
    }

    @Override
    public int getLeastMaximum(int field) {
        return 0;
    }
    /**
     *
     */
    private static class UmmALQura
    {
        public static int[] gregorianToHijri(int yg, int mg, int dg){
            return g2HA(yg,mg,dg);
        }

        public static int[] hijriToGregorian(int yh, int mh, int dh) {
            int[] output = new int[4];
            int[] result = h2GA(new int[]{yh, mh, dh, 0, 0, 0, 0});
            output[0] = result[3];
            output[1] = result[4];
            output[2] = result[5];
            output[3] = result[6];
            return output;
        }

        /*
          CopyRight by Fayez Alhargan, 2000
          King Abdulaziz City for Science and Technology
          Computer and Electronics Research Institute
          Riyadh, Saudi Arabia
          alhargan@kacst.edu.sa
          Tel:4813770 Fax:4813764
          This is a program that computes the Hijary dates based on the moonset
          at Makkah.
          version: 1.01
          last modified 20-8-2000
        */
        private static int HStartYear = 1300;
        private static int HEndYear = 1600;
        private static int[] MonthMap = {
                17749, 12971, 14647, 17078, 13686, 17260, 15189, 19114, 18774, 13470, 14685, 17082, 13749, 17322, 19275, 19094, 17710, 12973, 13677, 19290,
                18258, 20261, 24202, 19734, 19030, 19125, 18100, 19881, 19346, 19237, 17995, 15003, 17242, 18137, 17876, 19877, 19786, 19093, 17718, 14709,
                17140, 18153, 18132, 18089, 17717, 12893, 13501, 14778, 17332, 19305, 19242, 19029, 17581, 14941, 17114, 14041, 20138, 24212, 19754, 19542,
                17582, 14957, 17770, 19797, 19786, 19091, 13611, 14939, 17722, 14005, 20137, 23890, 19753, 19029, 17581, 13677, 19178, 18148, 20177, 23970,
                19114, 18778, 17114, 13753, 19378, 18276, 18121, 17749, 12971, 13531, 19130, 17844, 19881, 23890, 19109, 18733, 12909, 14573, 17114, 15061,
                19109, 19019, 13463, 14647, 17078, 14709, 19817, 19794, 19605, 18731, 12891, 13531, 18901, 17874, 19877, 19786, 19093, 17741, 15021, 17322,
                19410, 19396, 19337, 19093, 13613, 13741, 15210, 18132, 19913, 19858, 19110, 18774, 12974, 13677, 13162, 15189, 19114, 14669, 13469, 14685,
                12986, 13749, 17834, 15701, 19098, 14638, 12910, 13661, 15066, 18132, 18085, 13643, 14999, 17742, 15022, 17836, 15273, 19858, 19237, 13899,
                15531, 17754, 15189, 18130, 16037, 20042, 19093, 13613, 15021, 17260, 14169, 18130, 18069, 13613, 14939, 13498, 14778, 17332, 15209, 19282,
                19110, 13494, 14701, 17132, 14041, 20146, 19796, 19754, 19030, 13486, 14701, 19818, 19284, 19241, 14995, 13611, 14935, 13622, 15029, 18090,
                16019, 19733, 17963, 15451, 17722, 14005, 19890, 23908, 19753, 19029, 17581, 14701, 19178, 18152, 20177, 23972, 19786, 19050, 17114, 13753,
                19314, 23400, 18129, 18005, 13483, 14683, 17082, 13749, 19881, 23890, 19622, 18766, 17518, 14685, 17626, 15061, 19114, 19021, 13467, 14647,
                17590, 14709, 19818, 19794, 19109, 18763, 12971, 13659, 19161, 17874, 19909, 23954, 19237, 17749, 15029, 17844, 19369, 18338, 18245, 17811,
                15019, 17622, 18902, 17874, 19365, 19274, 19093, 17581, 12637, 13021, 18906, 17844, 17833, 13613, 12891, 14519, 12662, 13677, 19306, 19146,
                19094, 17707, 12635, 12987, 13750, 19882, 23444, 19782, 19085, 17709, 15005, 17754, 14165, 18249, 20243, 20042, 19094, 17750, 14005, 19370,
                23444 };
        private static short[] gmonth = { 31, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, 31 };

        /****************************************************************************/
	    /* Name:    BH2GA                                                            */
	    /* Type:    Procedure                                                       */
	    /* Purpose: Finds Gdate(year,month,day) for Hdate(year,month,day=1)  	    */
	    /*   Computation Based  on Store data  MonthMap                             */
	    /* Arguments:                                                               */
	    /* Input: Hijrah  date: year:yh, month:mh                                   */
	    /* Output: Gregorian date: year:yg, month:mg, day:dg , day of week:dayweek  */
	    /*       and returns flag found:1 not found:0                               */
        /****************************************************************************/
        private static int[] bH2GA(int yh, int mh){
            int[] out = new int[4];
            int Dy, m;
            long JD;
            double GJD;
	        /* Make sure that the date is within the range of the tables */
            if (mh < 1)
            {
                mh = 12;
            }

            if (mh > 12)
            {
                mh = 1;
            }

            if (yh < HStartYear)
            {
                yh = HStartYear;
            }

            if (yh > HEndYear)
            {
                yh = HEndYear;
            }
            Double doubleHolder = hCalendarToJD(yh,1,1);
            JD = doubleHolder.longValue();
	        /* estimate JD of the begining of the year */
            Dy = MonthMap[yh - HStartYear] / 4096;
	        /* Mask 1111000000000000 */
            GJD = JD - 3 + Dy;
            for (m = 1;m < mh;m++){
                GJD = GJD + getMonthLength(yh, m);
            }
            int[] result = jDToGCalendar(GJD);
            out[0] = result[0];
            out[1] = result[1];
            out[2] = result[2];
            doubleHolder = GJD;
            JD = doubleHolder.longValue();
            Long longHolder = (JD + 1) % 7;
            out[3] = longHolder.intValue();
            return out;
        }

        /**
         *
         * @param yh Hijri year
         * @param mh Hijri month
         * @param dh Hijri day
         * @return DAY_OF_WEEK
         */
        private static int getDayOfWeek(int yh, int mh, int dh){
            Double doubleHolder = hCalendarToJD(yh,1,1)-1;
            for (int i = 1; i < mh; i++) {
                doubleHolder += getMonthLength(yh, i);
            }
            doubleHolder+=dh;
            //Fix 2015 bug. there is 2020 bug
            if(doubleHolder > 2456955.375512 && doubleHolder < 2457310.109648)
                return ((doubleHolder.intValue()+2)%7)+1;
            return ((doubleHolder.intValue()+1)%7)+1;
        }

        /**
         *
         * @param yh Hijri year
         * @param mh Hijri month
         * @param dh Hijri day
         * @return DAY_OF_YEAR
         */
        private static int getDayOfYear(int yh, int mh, int dh){
            int counter = dh;
            for (int i = 1; i < mh; i++) {
                counter += getMonthLength(yh, i);
            }
            return counter;
        }

        /**
         * Obtains the month length (in days)
         * @param yh Hijri year
         * @param mh Hijri month
         * @return Month Length (29 or 30)
         */
        private static int getMonthLength(int yh, int mh) {
            int flag, Dy, N, m;
            if (yh < HStartYear || yh > HEndYear)
            {
                flag = 0;
                Dy = 0;
            }
            else
            {
                N = 1;
                for (m = 1;m < mh;m++)
                    N = 2 * N;
                flag = MonthMap[yh - HStartYear] & N;
                // Mask for the current month //
                if (flag != 0)
                    Dy = 30;
                else
                    Dy = 29;
            }
            return Dy;
        }

        /****************************************************************************/
	    /* Name:    G2HA                                                            */
	    /* Type:    Procedure                                                       */
	    /* Purpose: convert Gdate(year,month,day) to Hdate(year,month,day)          */
	    /* Arguments:                                                               */
	    /* Input: Gregorian date: year:yg, month:mg, day:dg                         */
	    /* Output: Hijrah  date: year:yh, month:mh, day:dh, day of week:dayweek     */
	    /*       and returns flag found:1 not found:0                               */
        /****************************************************************************/
        private static int[] g2HA(int yg, int mg, int dg){
            int[] out = new int[4];
            int yh1 = 0, mh1 = 0, dh1 = 0;
            int yh2, mh2, dh2;
            int yg1 = 0, mg1 = 0, dg1 = 0;
            int yg2 = 0, mg2 = 0, dg2 = 0;
            int found;
            int flag;
            long J;
            double GJD;
            GJD = gCalendarToJD(yg, mg, dg + 0.5);
	        /* find JD of Gdate */
            int[] jDToHC = jDToHCalendar(GJD);
            yh1 = jDToHC[0];
            mh1 = jDToHC[1];
            dh1 = jDToHC[2];
	        /* estimate the Hdate that correspond to the Gdate */
            found = 0;
            flag = 1;
            while ((!(found == 1)) && (flag == 1))
            {
	            /* start searching for the exact Hdate */
                int[] result = h2GA(new int[]{yh1, mh1, dh1, yg1, mg1, dg1, out[3]});
                yh1 = result[0];
                mh1 = result[1];
                dh1 = result[2];
                yg1 = result[3];
                mg1 = result[4];
                dg1 = result[5];
                out[3] = result[6];
	            /* compute the exact correponding Gdate for the dh1-mh1-yh1 */
                if (yg1 > yg)
                {
                    dh1--;
                    if (dh1 < 1)
                    {
                        dh1 = 29 + dh1;
                        mh1--;
                    }

                }

                if (yg1 < yg)
                {
                    dh1++;
                    if (dh1 > 30)
                    {
                        dh1 = dh1 - 30;
                        mh1++;
                    }

                    if (dh1 == 30)
                    {
                        dh2 = 1;
                        mh2 = mh1 + 1;
                        yh2 = yh1;
                        if (mh2 > 12)
                        {
                            yh2++;
                            mh2 = mh2 - 12;
                        }

                        int[] result2 = h2GA(new int[]{yh2, mh2, dh2, yg2, mg2, dg2, out[3]});
                        yh2 = result2[0];
                        mh2 = result2[1];
                        dh2 = result2[2];
                        yg2 = result2[3];
                        mg2 = result2[4];
                        dg2 = result2[5];
                        out[3] = result2[6];
                        if (dg2 == dg)
                        {
                            mh1++;
                            dh1 = 1;
                        }

                    }

                }

	            /* check to see that if 30 is actually 1st of next month */
                if (yg1 == yg)
                {
                    if (mg1 > mg)
                    {
                        dh1--;
                        if (dh1 < 1)
                        {
                            dh1 = 29 + dh1;
                            mh1--;
                        }

                    }

                    if (mg1 < mg)
                    {
                        dh1++;
                        if (dh1 > 30)
                        {
                            dh1 = dh1 - 30;
                            mh1++;
                        }

                        if (dh1 == 30)
                        {
                            dh2 = 1;
                            mh2 = mh1 + 1;
                            yh2 = yh1;
                            if (mh2 > 12)
                            {
                                yh2++;
                                mh2 = mh2 - 12;
                            }

                            int[] result2 = h2GA(new int[]{yh2, mh2, dh2, yg2, mg2, dg2, out[3]});
                            yh2 = result2[0];
                            mh2 = result2[1];
                            dh2 = result2[2];
                            yg2 = result2[3];
                            mg2 = result2[4];
                            dg2 = result2[5];
                            out[3] = result2[6];
                            if (dg2 == dg)
                            {
                                mh1++;
                                dh1 = 1;
                            }

                        }

                    }

	                /* check to see that if 30 is actually 1st of next month */
                    if (mg1 == mg && yg1 == yg)
                    {
	                    /* if the months are equal than adjust the days */
                        found = 1;
                        if (dg1 > dg)
                        {
                            dh1 = dh1 - (dg1 - dg);
                            found = 0;
                        }

                        if (dg1 < dg)
                        {
                            dh1 = dh1 - (dg1 - dg);
                            found = 0;
                        }

                        if (dh1 < 1)
                        {
                            dh1 = 29 + dh1;
                            mh1--;
                        }

                        if (dh1 > 30)
                        {
                            dh1 = dh1 - 30;
                            mh1++;
                        }

                        if (dh1 == 30)
                        {
                            dh2 = 1;
                            mh2 = mh1 + 1;
                            yh2 = yh1;
                            if (mh2 > 12)
                            {
                                yh2++;
                                mh2 = mh2 - 12;
                            }

                            int[] result2 = h2GA(new int[]{yh2, mh2, dh2, yg2, mg2, dg2, out[3]});
                            yh2 = result2[0];
                            mh2 = result2[1];
                            dh2 = result2[2];
                            yg2 = result2[3];
                            mg2 = result2[4];
                            dg2 = result2[5];
                            out[3] = result2[6];
                            if (dg2 == dg)
                            {
                                mh1++;
                                dh1 = 1;
                            }

                        }

                    }

                }

	            /* check to see that if 30 is actually 1st of next month */
                if (mh1 < 1)
                {
                    yh1--;
                    mh1 = 12 + mh1;
                }

                if (mh1 > 12)
                {
                    yh1++;
                    mh1 = mh1 - 12;
                }

            }
            Double doubleHolder = gCalendarToJD(yg, mg, dg) + 2;
            J = doubleHolder.longValue();
            Long longHolder = J % 7;
            out[3] = longHolder.intValue();
            out[0] = yh1;
            out[1] = mh1;
            out[2] = dh1;
            return out;
        }

        /****************************************************************************/
	    /* Name:    H2GA                                                            */
	    /* Type:    Procedure                                                       */
	    /* Purpose: convert Hdate(year,month,day) to Gdate(year,month,day)          */
	    /* Arguments:                                                               */
	    /* Input/Ouput: Hijrah  date: year:yh, month:mh, day:dh                     */
	    /* Output: Gregorian date: year:yg, month:mg, day:dg , day of week:dayweek  */
	    /*       and returns flag found:1 not found:0                               */
	    /* Note: The function will correct Hdate if day=30 and the month is 29 only */
        /****************************************************************************/
        /**
         * convert Hijri date to Gregorian date
         * @param vals
         * @return int[] where indexes are 0=Hijri year, 1=Hijri month, 2=Hijri day, 3=Gregorian year , 4=Gregorian month, 5=Gregorian day, 6=Day of Week
         */
        private static int[] h2GA(int[] vals){
            //yh=0, mh=1, dh=2, yg=3, mg=4, dg=5, dayweek=6
            int[] out = vals;
            int yh1 = 0, mh1 = 0/*, yg1 = 0, mg1 = 0, dg1 = 0, dw1 = 0*/;
	        /*find the date of the begining of the month*/
	        /* make sure values are within the allowed values */
            if (out[2] > 30)
            {
                out[2] = 1;
                out[1]++;
            }

            if (out[2] < 1)
            {
                out[2] = 1;
                out[1]--;
            }

            if (out[1] > 12)
            {
                out[1] = 1;
                out[0]++;
            }

            if (out[1] < 1)
            {
                out[1] = 12;
                out[0]--;
            }

            int[] result = bH2GA(out[0],out[1]);
            out[3] = result[0];
            out[4] = result[1];
            out[5] = result[2];
            out[6] = result[3];
            out[5] = out[5] + out[2] - 1;
            int[] resultGDate = gDateAjust(out[3],out[4],out[5]);
            out[3] = resultGDate[0];
            out[4] = resultGDate[1];
            out[5] = resultGDate[2];
	        /* Make sure that dates are within the correct values */
            out[6] = out[6] + out[2] - 1;
            out[6] = out[6] % 7;
	        /*find the date of the begining of the next month*/
            if (out[2] == 30)
            {
                mh1 = out[1] + 1;
                yh1 = out[0];
                if (mh1 > 12)
                {
                    mh1 = mh1 - 12;
                    yh1++;
                }

                result = bH2GA(yh1,mh1);
                if (out[5] == result[2])
                {
                    out[0] = yh1;
                    out[1] = mh1;
                    out[2] = 1;
                }

            }

            return out;
        }

        /****************************************************************************/
	    /* Name:    JDToGCalendar						    */
	    /* Type:    Procedure                                                       */
	    /* Purpose: convert Julian Day  to Gdate(year,month,day)                    */
	    /* Arguments:                                                               */
	    /* Input:  The Julian Day: JD                                               */
	    /* Output: Gregorian date: year:yy, month:mm, day:dd                        */
        /****************************************************************************/
        private static int[] jDToGCalendar(double JD){
            int[] out = new int[3];
            double A, B, F;
            int alpha, C, E;
            long D, Z;
            Z = (long)Math.floor(JD + 0.5);
            F = (JD + 0.5) - Z;
            alpha = (int)((Z - 1867216.25) / 36524.25);
            A = Z + 1 + alpha - alpha / 4;
            B = A + 1524;
            C = (int)((B - 122.1) / 365.25);
            D = (long)(365.25 * C);
            E = (int)((B - D) / 30.6001);
            Double holder = B - D - Math.floor(30.6001 * E) + F;
            out[2] = holder.intValue();
            if (E < 14)
                out[1] = E - 1;
            else
                out[1] = E - 13;
            if (out[1] > 2)
                out[0] = C - 4716;
            else
                out[0] = C - 4715;
            F = F * 24.0;
            return out;
        }

        /****************************************************************************/
	    /* Name:    GCalendarToJD						    */
	    /* Type:    Function                                                        */
	    /* Purpose: convert Gdate(year,month,day) to Julian Day            	    */
	    /* Arguments:                                                               */
	    /* Input : Gregorian date: year:yy, month:mm, day:dd                        */
	    /* Output:  The Julian Day: JD                                              */
        /****************************************************************************/
        private static double gCalendarToJD(int yy, int mm, double dd){
	        /* it does not take care of 1582correction assumes correct calender from the past  */
            int A, B, m, y;
            double T1, T2, Tr;
            if (mm > 2)
            {
                y = yy;
                m = mm;
            }
            else
            {
                y = yy - 1;
                m = mm + 12;
            }
            A = y / 100;
            B = 2 - A + A / 4;
            T1 = ip(365.25 * (y + 4716));
            T2 = ip(30.6001 * (m + 1));
            Tr = T1 + T2 + dd + B - 1524.5;
            return Tr;
        }

        /****************************************************************************/
	    /* Name:    GLeapYear						            */
	    /* Type:    Function                                                        */
	    /* Purpose: Determines if  Gdate(year) is leap or not            	    */
	    /* Arguments:                                                               */
	    /* Input : Gregorian date: year				                    */
	    /* Output:  0:year not leap   1:year is leap                                */
        /****************************************************************************/
        private static int gLeapYear(int year){
            int T;
            T = 0;
            if (year % 4 == 0)
                T = 1;

	        /* leap_year=1; */
            if (year % 100 == 0)
            {
                T = 0;
	            /* years=100,200,300,500,... are not leap years */
                if (year % 400 == 0)
                    T = 1;

            }

            return T;
        }

	    /*  years=400,800,1200,1600,2000,2400 are leap years */
        /****************************************************************************/
	    /* Name:    GDateAjust							    */
	    /* Type:    Procedure                                                       */
	    /* Purpose: Adjust the G Dates by making sure that the month lengths        */
	    /*	    are correct if not so take the extra days to next month or year */
	    /* Arguments:                                                               */
	    /* Input: Gregorian date: year:yg, month:mg, day:dg                         */
	    /* Output: corrected Gregorian date: year:yg, month:mg, day:dg              */
        /****************************************************************************/
        private static int[] gDateAjust(int yg, int mg, int dg){
            int[] out = new int[3];
            int dys;
            //
            out[0] = yg;
            out[1] = mg;
            out[2] = dg;
	        /* Make sure that dates are within the correct values */
	        /*  Underflow  */
            if (out[1] < 1)
            {
	            /* months underflow */
                out[1] += 12;
	            /* plus as the underflow months is negative */
                out[0]--;
            }

            if (out[2] < 1)
            {
	            /* days underflow */
                out[1]--;
	            /* month becomes the previous month */
                out[2] += gmonth[out[1]];
	            /* number of days of the month less the underflow days (it is plus as the sign of the day is negative) */
                if (out[1] == 2)
                    out[2] += gLeapYear(out[0]);

                if (out[1] < 1)
                {
	                /* months underflow */
                    out[1] += 12 ;
	                /* plus as the underflow months is negative */
                    out[0]--;
                }

            }

	        /* Overflow  */
            if (out[1] > 12)
            {
	            /* months */
                out[1] -= 12;
                out[0]++;
            }

            if (out[1] == 2)
                dys = gmonth[out[1]] + gLeapYear(out[0]);
            else
	            /* number of days in the current month */
                dys = gmonth[out[1]];
            if (out[2] > dys)
            {
	            /* days overflow */
                out[2] -= dys;
                out[1]++;
                if (out[1] == 2)
                {
                    dys = gmonth[out[1]] + gLeapYear(out[0]);
	                /* number of days in the current month */
                    if (out[2] > dys)
                    {
                        out[2] -= dys;
                        out[1]++;
                    }

                }

                if (out[1] > 12)
                {
	                /* months */
                    out[1] -= 12;
                    out[0]++;
                }

            }
            return out;
        }

        /****************************************************************************/
	    /* Name:    HCalendarToJD						    */
	    /* Type:    Function                                                        */
	    /* Purpose: convert Hdate(year,month,day) to estimated Julian Day     	    */
	    /* Arguments:                                                               */
	    /* Input : Hijrah  date: year:yh, month:mh, day:dh                          */
	    /* Output:  The Estimated Julian Day: JD                                    */
        /****************************************************************************/
        private static double hCalendarToJD(int yh, int mh, int dh) {
	        /*
	           Estimating The JD for hijrah dates
	           this is an approximate JD for the given hijrah date
	         */
            double md, yd;
            md = (mh - 1.0) * 29.530589;
            yd = (yh - 1.0) * 354.367068 + md + dh - 1.0;
            yd = yd + 1948439.0;
            return yd;
        }

	    /*  add JD for 18/7/622 first Hijrah date */
        /****************************************************************************/
	    /* Name:    JDToHCalendar						    */
	    /* Type:    Procedure                                                       */
	    /* Purpose: convert Julian Day to estimated Hdate(year,month,day)	    */
	    /* Arguments:                                                               */
	    /* Input:  The Julian Day: JD                                               */
	    /* Output : Hijrah date: year:yh, month:mh, day:dh                          */
        /****************************************************************************/
        private static int[] jDToHCalendar(double JD){
            int[] out = new int[3];
	    	/*
	           Estimating the hijrah dates from JD
	         */
            double md, yd;
            yd = JD - 1948439.0;
	        /*  subtract JD for 18/7/622 first Hijrah date*/
            md = mod(yd,354.367068);
            out[2] = mod(md + 0.5,29.530589) + 1;
            Double doubleHolder = (md / 29.530589) + 1;
            out[1] = doubleHolder.intValue();
            yd = yd - md;
            doubleHolder = yd / 354.367068 + 1;
            out[0] = doubleHolder.intValue();
            if (out[2] > 30){
                out[2] -= 30;
                out[1]++;
            }

            if (out[1] > 12){
                out[1] -= 12;
                out[0]++;
            }

            return out;
        }

        /**************************************************************************/
        private static double ip(double x) {
	        /* Purpose: return the integral part of a double value.     */
            //  double  tmp;
            // modf(x, &tmp);
            //return tmp;
            double result = round(x);
            return result;
        }

        /**************************************************************************/
	    /*
	      Name: mod
	      Purpose: The mod operation for doubles  x mod y
	    */
        private static int mod(double x, double y){
            int r;
            double d;
            d = x / y;
            Double doubleHolder = round(d);
            r = doubleHolder.intValue();
            if (r < 0)
                r--;

            d = x - y * r;
            doubleHolder = round(d);
            r = doubleHolder.intValue();
            return r;
        }

        private static double round(double d, int decimalPlace) {
            BigDecimal bd = new BigDecimal(d);
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bd.doubleValue();
        }

        private static double round(double d) {
            return round(d, 0);
        }
    }
}