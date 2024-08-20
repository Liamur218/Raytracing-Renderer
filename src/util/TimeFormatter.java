package util;

public class TimeFormatter {

    private static final TimeUnit DAY = new TimeUnit("day", Long.MAX_VALUE, null);
    private static final TimeUnit HOUR = new TimeUnit("hr", 24, DAY);
    private static final TimeUnit MINUTE = new TimeUnit("min", 60, HOUR);
    private static final TimeUnit SECOND = new TimeUnit("sec", 60, MINUTE);
    private static final TimeUnit MILLISECOND = new TimeUnit("ms", 1000, SECOND);

    private static final TimeUnit START_UNIT = MILLISECOND;

    public static String timeToString(long timeNs) {
        long time = (long) (timeNs * 1E-6);
        if (time == 0) {
            return "< 0 ms";
        }
        StringBuilder out = new StringBuilder();
        for (TimeUnit timeUnit = MILLISECOND; timeUnit != null && time > 0; timeUnit = timeUnit.nextUnit) {
            long unitTime = time % timeUnit.maxCount;
            time /= timeUnit.maxCount;
            out.insert(0, unitTime + " " + timeUnit.unitName + ((timeUnit == START_UNIT) ? "" : ", "));
        }
        return out.toString();
    }

    private static class TimeUnit {

        String unitName;
        long maxCount;
        TimeUnit nextUnit;

        TimeUnit(String unitName, long maxCount, TimeUnit nextUnit) {
            this.unitName = unitName;
            this.maxCount = maxCount;
            this.nextUnit = nextUnit;
        }
    }
}
