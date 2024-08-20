package util;

import renderer.RenderSettings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public abstract class Debug {

    private static boolean verbose = false;

    private static final DecimalFormat DF = new DecimalFormat("###.###");

    private static final StringBuilder log;

    static {
        log = new StringBuilder();
    }

    public static void setPrintLogs(boolean status) {
        verbose = status;
    }

    public static void logWarningMsg(String warningMsg) {
        logMsgLn("[WARNING] " + warningMsg);
    }

    public static void logErrorMsg(String errorMsg) {
        logMsgLn("[ERROR] " + errorMsg);
    }

    public static void logMsgLn(String string) {
        log.append(string).append("\n");
        if (verbose) {
            print(string + "\n");
        }
    }

    public static void logMsg(String string) {
        log.append(string);
        if (verbose) {
            print(string);
        }
    }

    public static void printMsgLn(String string) {
        if (verbose) {
            print(string + "\n");
        }
    }

    public static void printMsg(String string) {
        if (verbose) {
            print(string);
        }
    }

    private static void print(String msg) {
        System.out.print(msg.replace(
                "ERROR", ConsoleColors.RED + "ERROR" + ConsoleColors.RESET).replace(
                        "WARNING", ConsoleColors.YELLOW + "WARNING" + ConsoleColors.YELLOW));
    }

    public static void logElapsedTime(String title, long startTime) {
        logElapsedTime(title, startTime, System.nanoTime());
    }

    public static void logElapsedTime(String title, long startTime, long endTime) {
        String elapsedTime = getElapsedTime(title, startTime, endTime) + "\n";
        log.append(elapsedTime);
        if (verbose) {
            print(elapsedTime);
        }
    }

    private static String getElapsedTime(String title, long startTime, long endTime) {
//        StringBuilder stringBuilder = new StringBuilder();
//        double time = (endTime - startTime) / 1E9;
//        stringBuilder.append(title).append(DF.format(time)).append(" sec").append("\n");
//        if (time > 60) {
//            stringBuilder.append(" ".repeat(title.length()));
//            stringBuilder.append(((int) time / 60)).append(" min ").append(DF.format(time % 60)).append(" sec");
//            stringBuilder.append("\n");
//        }
//        return stringBuilder.toString();
        return title + TimeFormatter.timeToString(endTime - startTime);
    }

    public static void writeLogsToFile(RenderSettings settings) {
        writeLogsToFile(settings.toFilenameString() + ".txt");
    }

    public static void writeLogsToFile(String filepath) {
        String filename = "renders/" + filepath;
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
            out.println(log.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (NullPointerException ignored) {
            }
        }
    }
}
