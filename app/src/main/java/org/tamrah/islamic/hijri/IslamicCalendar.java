package org.tamrah.islamic.hijri;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author abdullah alfadhel
 * @version 0.0.2b
 **/

public class IslamicCalendar extends Calendar implements Hijri {
    // Proclaim serialization
    private static final long serialVersionUID = 4621845246912192901L;
    protected int different = 0;

    public static IslamicCalendar getInstance(){
        return new IslamicCalendar(Calendar.getInstance());
    }

    /**
     *
     * @param diff different day
     * @return
     */
    public static IslamicCalendar getInstance(int diff){
        return new IslamicCalendar(Calendar.getInstance(), diff);
    }

    //
    public static IslamicCalendar getInstance(Locale locale){
        return new IslamicCalendar(Calendar.getInstance(locale));
    }

    public static IslamicCalendar getInstance(Locale locale, int diff){
        return new IslamicCalendar(Calendar.getInstance(locale), diff);
    }

    public IslamicCalendar(Calendar calendar){
        gregorianToHijri(calendar, 0);
    }

    public IslamicCalendar(Calendar calendar, int diff){
        gregorianToHijri(calendar, diff);
    }
    /**
     *
     * @param year Hijri year
     * @param month Hijri month
     * @param day Hijri day
     */
    public IslamicCalendar(int year, int month, int day){
        fields[ERA] = AH;
        fields[YEAR] = year;
        fields[MONTH] = month;
        set(DAY_OF_MONTH, day);
    }

    /**
     *
     * @param year Hijri year
     * @param month Hijri month
     * @param day Hijri day
     * @param diff different day
     */
    public IslamicCalendar(int year, int month, int day, int diff){
        fields[ERA] = AH;
        fields[YEAR] = year;
        fields[MONTH] = month;
        set(DAY_OF_MONTH, day);
        different = diff;
    }

    @Override
    public Calendar toGregorianCalendar() {
        Calendar calendar = Calendar.getInstance();
        int[] date = islToGer(fields[YEAR], fields[MONTH]-1, fields[DATE], different);
        calendar.set(YEAR, date[2]);
        calendar.set(MONTH, date[1]);
        calendar.set(DATE, date[0]);
        //Set time
        for (int i = AM_PM; i < FIELD_COUNT; i++) {
            calendar.set(i, fields[i]);
        }
        return calendar;
    }

    @Override
    protected void computeTime() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void computeFields() {
        fields[DAY_OF_WEEK] = toGregorianCalendar().get(DAY_OF_WEEK);
    }

    @Override
    public void set(int field, int value) {
        if(field == YEAR && value < 0){
            fields[YEAR] = -value;
            fields[ERA] = BH;
        }else
            fields[field] = value;
        if(field == YEAR || field == MONTH || field == DATE)
            computeFields();
    }

    @Override
    public void setWithoutComputeFields(int field, int value) {
        if(field == YEAR && value < 0){
            fields[YEAR] = -value;
            fields[ERA] = BH;
        }else
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
            case DAY_OF_MONTH:
                Calendar calendarDAY_OF_MONTH = toGregorianCalendar();
                calendarDAY_OF_MONTH.add(DAY_OF_MONTH, amount);
                gregorianToHijri(calendarDAY_OF_MONTH, different);
                break;
            case DAY_OF_YEAR:
                Calendar calendarDAY_OF_YEAR = toGregorianCalendar();
                calendarDAY_OF_YEAR.add(DAY_OF_YEAR, amount);
                gregorianToHijri(calendarDAY_OF_YEAR, different);
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
                if(internalGet(DATE) == 30 && getDaysMonth(internalGet(YEAR), internalGet(MONTH)) != 30)
                    set(DATE, 29);
                break;
            default:
                set(field, internalGet(field) + amount);
                break;
        }
    }

    @Override
    public void addWithoutComputeFields(int field, int amount) {
        // If amount == 0, do nothing even the given field is out of
        if (amount == 0) {
            return;   // Do nothing!
        }

        if (field < 0 || field >= ZONE_OFFSET) {
            throw new IllegalArgumentException();
        }

        switch (field) {
            case DAY_OF_MONTH:
                Calendar calendarDAY_OF_MONTH = toGregorianCalendar();
                calendarDAY_OF_MONTH.add(DAY_OF_MONTH, amount);
                gregorianToHijri(calendarDAY_OF_MONTH, different);
                break;
            case DAY_OF_YEAR:
                Calendar calendarDAY_OF_YEAR = toGregorianCalendar();
                calendarDAY_OF_YEAR.add(DAY_OF_YEAR, amount);
                gregorianToHijri(calendarDAY_OF_YEAR, different);
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
                if(internalGet(DATE) == 30 && getDaysMonth(internalGet(YEAR), internalGet(MONTH)) != 30)
                    setWithoutComputeFields(DATE, 29);
                break;
            default:
                setWithoutComputeFields(field, internalGet(field) + amount);
                break;
        }
    }

    @Override
    public void roll(int field, boolean up) {
        // TODO Auto-generated method stub

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
                ret = 0;
                break;
//		case ERA:
//			ret = BH;
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
                ret = 9999;
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
                ret = getDaysMonth(get(YEAR), get(MONTH));
                break;
            case DAY_OF_WEEK:
                ret = 7;
            case MONTH:
                ret = 12;
                break;
            case YEAR:
                ret = 9999;
                break;
//		case ERA:
//			ret = AH;
//			break;
        }
        return ret;
    }

    @Override
    public int getGreatestMinimum(int field) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getLeastMaximum(int field) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getDifferent() {
        return different;
    }
    //TODO
    //utility function
    int intPart(int floatNum){
        if ((float)floatNum < -0.0000001){
            return (int) Math.ceil(floatNum-0.0000001);
        }
        return (int)Math.floor(floatNum +0.0000001);
    }

    //Gregorian to islamic calendar
    private int[] greToIsl(int y, int m, int d, int diff) {
        int jd = 0;
        if ((y>1582)||((y==1582)&&(m>10))||((y==1582)&&(m==10)&&(d>14))) {
            jd=intPart((1461*(y+4800+intPart((m-14)/12)))/4)+intPart((367*(m-2-12*(intPart((m-14)/12))))/12)-
                    intPart( (3* (intPart(  (y+4900+    intPart( (m-14)/12)     )/100)    )   ) /4)+d-32075;
        }
        else{
            jd = 367*y-intPart((7*(y+5001+intPart((m-9)/7)))/4)+intPart((275*m)/9)+d+1729777;
        }

        int l=jd-1948440+10632;
        int n=intPart((l-1)/10631);
        l=l-10631*n+354+diff;
        int j=(intPart((10985-l)/5316))*(intPart((50*l)/17719))+(intPart(l/5670))*(intPart((43*l)/15238));
        l=l-(intPart((30-j)/15))*(intPart((17719*j)/50))-(intPart(j/16))*(intPart((15238*j)/43))+29;
        m=intPart((24*l)/709);
        d=l-intPart((709*m)/24);
        y=30*n+j-30;


        int[] res = new int [3];
        res[0] = d;
        res[1] = m;
        res[2] = y;
        return res;
    }

    //islamic to Gregorian calendar
    private int[] islToGer(int y, int m, int d, int diff) {
        int jd=intPart((11*y+3)/30)+354*y+30*m-intPart((m-1)/2)+d+1948440-385-diff;
        if (jd> 2299160 ){
            int l=jd+68569;
            int n=intPart((4*l)/146097);
            l=l-intPart((146097*n+3)/4);
            int i=intPart((4000*(l+1))/1461001);
            l=l-intPart((1461*i)/4)+31;
            int j=intPart((80*l)/2447);
            d=l-intPart((2447*j)/80);
            l=intPart(j/11);
            m=j+2-12*l;
            y=100*(n-49)+i+l;
        }else{
            int j=jd+1402;
            int k=intPart((j-1)/1461);
            int l=j-1461*k;
            int n=intPart((l-1)/365)-intPart(l/1461);
            int i=l-365*n+30;
            j=intPart((80*i)/2447);
            d=i-intPart((2447*j)/80);
            i=intPart(j/11);
            m=j+2-12*i;
            y=4*k+n+i-4716;
        }

        int[] res = new int [3];
        res[0] = d;
        res[1] = m;
        res[2] = y;
        return res;
    }

    /*
     *
     */
    protected void gregorianToHijri(Calendar calendar, int diff) {
        different = diff;
        int y = calendar.get(YEAR);
        int m = calendar.get(MONTH);
        int d = calendar.get(DATE);

        int[] date = greToIsl(y, m, d, diff);

        fields[ERA] = AH;
        fields[YEAR] = date[2];
        fields[MONTH] = date[1]+1;
        fields[DAY_OF_MONTH] = date[0];
        fields[DAY_OF_WEEK] = calendar.get(DAY_OF_WEEK);
    }

    private boolean isALeapYear(int year)
    {
        int modValue = year % 30;
        switch (modValue)
        {
            case 2:
                return true;
            case 5:
                return true;
            case 7:
                return true;
            case 10:
                return true;
            case 13:
                return true;
            case 15:
                return true;
            case 18:
                return true;
            case 21:
                return true;
            case 24:
                return true;
            case 26:
                return true;
            case 29:
                return true;
        }
        return false;
    }

    private int getDaysMonth(int year, int month){
        if (month % 2 != 0)
            return 30;
        else{
            if (month == DHU_AL_HIJJAH && isALeapYear(year))
                return 30;
            return 29;
        }
    }

}