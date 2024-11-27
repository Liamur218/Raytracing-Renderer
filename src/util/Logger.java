package util;

import renderer.RenderSettings;

import java.io.*;
import java.text.DecimalFormat;

public class Logger {

    private boolean verbose;

    private final DecimalFormat DF = new DecimalFormat("###.###");

    private final StringBuilder log;
    private LogSection currentSection = null;

    private static final String OUTPUT_DIR = "output/logs/";

    public Logger() {
        log = new StringBuilder();
        setVerbose(true);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void logWarningMsg(String warningMsg) {
        logMsgLn("[WARNING] " + warningMsg);
    }

    public void logErrorMsg(String errorMsg) {
        logMsgLn("[ERROR] " + errorMsg);
    }

    public void logMsgLn(String string) {
        log.append(string).append("\n");
        if (verbose) {
            print(string + "\n");
        }
    }

    public void logMsg(String string) {
        log.append(string);
        if (verbose) {
            print(string);
        }
    }

    public void printMsgLn(String string) {
        if (verbose) {
            print(string + "\n");
        }
    }

    public void printMsg(String string) {
        if (verbose) {
            print(string);
        }
    }

    private void print(String msg) {
        System.out.print(msg.replace(
                "ERROR", ConsoleColors.RED + "ERROR" + ConsoleColors.RESET).replace(
                "WARNING", ConsoleColors.YELLOW + "WARNING" + ConsoleColors.YELLOW));
    }

    // Sections
    public void newLogSection(String sectionName, String startMsg) {
        if (currentSection != null) {
            endLogSection();
        }
        logMsg(Util.getCurrentTime() + " " + startMsg + "... ");
        currentSection = new LogSection(sectionName, System.nanoTime());
    }

    public void endLogSection() {
        logMsgLn("Done");
        logElapsedTime("-> " + currentSection.name + " complete in: ", currentSection.startTime);
        currentSection = null;
    }

    // Time logging
    public void logElapsedTime(String title, long startTime) {
        logElapsedTime(title, startTime, System.nanoTime());
    }

    public void logElapsedTime(String title, long startTime, long endTime) {
        String elapsedTime = getElapsedTime(title, startTime, endTime) + "\n";
        log.append(elapsedTime);
        if (verbose) {
            print(elapsedTime);
        }
    }

    private String getElapsedTime(String title, long startTime, long endTime) {
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
        settings.logger.writeLogsToFile(settings.toFilenameString() + ".txt");
    }

    public void writeLogsToFile(String filepath) {
        String filename = OUTPUT_DIR + filepath;
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
            out.println(log);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (NullPointerException ignored) {
            }
        }
    }

    private static class LogSection {

        private final String name;
        private final long startTime;

        LogSection(String name, long startTime) {
            this.name = name;
            this.startTime = startTime;
        }
    }
}
