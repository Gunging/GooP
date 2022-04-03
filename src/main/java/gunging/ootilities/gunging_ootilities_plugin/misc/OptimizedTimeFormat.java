package gunging.ootilities.gunging_ootilities_plugin.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OptimizedTimeFormat {
    public int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;

    public static String GetCurrentTime() {
        // Get the current time.
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH mm ss");
        return sdf.format(cal.getTime());
    }

    public static String GetWeekday() {

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            default: return "SUNDAY";
            case 2: return "MONDAY";
            case 3: return "TUESDAY";
            case 4: return "WEDNESDAY";
            case 5: return "THURSDAY";
            case 6: return "FRIDAY";
            case 7: return "SATURDAY";
        }
    }

    @NotNull public static OptimizedTimeFormat Current() { return new OptimizedTimeFormat(GetCurrentTime()); }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To substract, use the appropiate method.
     */
    public void AddSeconds(int s) {
        second += s;

        while (second >= 60) {

            second -= 60;
            AddMinutes(1);
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To substract, use the appropiate method.
     */
    public void AddMinutes(int m) {
        minute += m;

        while(minute >= 60) {

            minute -= 60;
            AddHours(1);
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To substract, use the appropiate method.
     */
    public void AddHours(int h) {
        hour += h;

        while(hour >= 24) {

            hour -= 24;
            AddDays(1);
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To substract, use the appropiate method.
     */
    public void AddDays(int d) {
        day += d;

        int perMonthMax = 30;
        if (month == 2) { perMonthMax = 28; }
        if (month == 1 ||
                month == 3 ||
                month == 5 ||
                month == 7 ||
                month == 8 ||
                month == 10 ||
                month == 12)
        { perMonthMax = 31; }

        while(day >= perMonthMax) {

            day -= perMonthMax;

            AddMonths(1);

            perMonthMax = 30;
            if (month == 2) { perMonthMax = 28; }
            if (month == 1 ||
                    month == 3 ||
                    month == 5 ||
                    month == 7 ||
                    month == 8 ||
                    month == 10 ||
                    month == 12)
            { perMonthMax = 31; }
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To substract, use the appropiate method.
     */
    public void AddMonths(int d) {
        month += d;

        while(month >= 12) {

            month -= 12;
            AddYears(1);
        }
    }

    /**
     * Actually it doesnt matter if its positive.
     * <p></p>
     * The rest of the methods do require that the argument is positive though.
     */
    public void AddYears(int y) {
        year += y;
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To forward, use the appropiate method.
     */
    public void RewindSeconds(int s) {
        second -= s;

        while (second < 0) {

            second += 60;
            RewindMinutes(1);
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To forward, use the appropiate method.
     */
    public void RewindMinutes(int m) {
        minute -= m;

        while(minute < 0) {

            minute += 60;
            RewindHours(1);
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To forward, use the appropiate method.
     */
    public void RewindHours(int h) {
        hour -= h;

        while(hour < 0) {

            hour += 24;
            RewindDays(1);
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To forward, use the appropiate method.
     */
    public void RewindDays(int d) {
        day -= d;

        int perMonthMax = 30;
        if (month == 2) { perMonthMax = 28; }
        if (month == 1 ||
                month == 3 ||
                month == 5 ||
                month == 7 ||
                month == 8 ||
                month == 10 ||
                month == 12)
           { perMonthMax = 31; }

        while(day < 0) {

            RewindMonths(1);

            perMonthMax = 30;
            if (month == 2) { perMonthMax = 28; }
            if (month == 1 ||
                    month == 3 ||
                    month == 5 ||
                    month == 7 ||
                    month == 8 ||
                    month == 10 ||
                    month == 12)
            { perMonthMax = 31; }

            day += perMonthMax;
        }
    }

    /**
     * MUST BE A POSITIVE AMOUNT.
     * <p></p>
     * To forward, use the appropiate method.
     */
    public void RewindMonths(int d) {
        month -= d;

        while(month < 0) {

            month += 12;
            RewindYears(1);
        }
    }

    /**
     * Actually it doesnt matter if its positive.
     * <p></p>
     * The rest of the methods do require that the argument is positive though.
     */
    public void RewindYears(int y) {
        year -= y;
    }

    @NotNull public static String toString(@Nullable OptimizedTimeFormat otf) {
        if (otf == null) { return ""; }
        return otf.year + " " + DateFormat(otf.month) + " " + DateFormat(otf.day) + " " + DateFormat(otf.hour) + " " + DateFormat(otf.minute) + " " + DateFormat(otf.second);
    }

    @Override
    public String toString() {
        return toString(this);
    }

    public OptimizedTimeFormat() { }

    public OptimizedTimeFormat(String time) {

        OptimizedTimeFormat ret = Convert(time);

        year = ret.year;
        month = ret.month;
        day = ret.day;
        hour = ret.hour;
        minute = ret.minute;
        second = ret.second;
    }
    public OptimizedTimeFormat(OptimizedTimeFormat ret) {
        year = ret.year;
        month = ret.month;
        day = ret.day;
        hour = ret.hour;
        minute = ret.minute;
        second = ret.second;
    }

    static String DateFormat(Integer source) {
        String str = source.toString();
        if (str.length() == 1) { return "0" + source; } else { return str; }
    }
    public static OptimizedTimeFormat Convert(String timeFormat) {

        OptimizedTimeFormat ret = new OptimizedTimeFormat();
        String[] d8 = timeFormat.split(" ");

        // Strip format
        if (d8.length == 6) {
            // Git
            ret.year = Integer.parseInt(d8[0]);

            ret.month = Integer.parseInt(d8[1]);

            ret.day = Integer.parseInt(d8[2]);

            ret.hour = Integer.parseInt(d8[3]);

            ret.minute = Integer.parseInt(d8[4]);

            ret.second = Integer.parseInt(d8[5]);

        } else {

            // Invalid
            return null;
        }

        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OptimizedTimeFormat)) { return false; }

        if (((OptimizedTimeFormat) obj).second != second) { return false; }
        if (((OptimizedTimeFormat) obj).minute != minute) { return false; }
        if (((OptimizedTimeFormat) obj).hour != hour) { return false; }
        if (((OptimizedTimeFormat) obj).day != day) { return false; }
        if (((OptimizedTimeFormat) obj).month != month) { return false; }
        return ((OptimizedTimeFormat) obj).year == year;
    }
}
