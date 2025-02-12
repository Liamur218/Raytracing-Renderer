package util;

public abstract class TimeFormatter {

    public static final TimeUnit DAY = new TimeUnit("day", Long.MAX_VALUE, null);
    public static final TimeUnit HOUR = new TimeUnit("hr", 24, DAY);
    public static final TimeUnit MINUTE = new TimeUnit("min", 60, HOUR);
    public static final TimeUnit SECOND = new TimeUnit("sec", 60, MINUTE);
    public static final TimeUnit MILLISECOND = new TimeUnit("ms", 1000, SECOND);

    private static final TimeUnit DEFAULT_START_UNIT = MILLISECOND;

    public static String timeToString(long timeNs) {
        return timeToString(timeNs, DEFAULT_START_UNIT);
    }

    public static String timeToString(long timeNs, TimeUnit startUnit) {
        long time = (long) (timeNs * 1E-6);
        if (time == 0) {
            return "< 1 ms";
        }
        StringBuilder out = new StringBuilder();
        for (TimeUnit timeUnit = MILLISECOND; timeUnit != null && time > 0; timeUnit = timeUnit.nextUnit) {
            long unitTime = time % timeUnit.maxCount;
            time /= timeUnit.maxCount;
            if (timeUnit.order <= startUnit.order) {
                out.insert(0, unitTime + " " + timeUnit.unitName + ((timeUnit == startUnit) ? "" : ", "));
            }
        }
        return out.toString();
    }

    public static class TimeUnit {

        private final String unitName;
        private final long maxCount;
        private final TimeUnit nextUnit;
        private final int order;
        private static int orderCounter = 0;

        private TimeUnit(String unitName, long maxCount, TimeUnit nextUnit) {
            this.unitName = unitName;
            this.maxCount = maxCount;
            this.nextUnit = nextUnit;
            order = orderCounter++;
        }
    }
}
