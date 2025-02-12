package util;

import renderer.RenderSettings;

import java.io.*;

public abstract class Logger {

    private static boolean verbose = true;

    private static final StringBuilder log = new StringBuilder();
    private static LogSection currentSection = null;

    private static final String OUTPUT_DIR = "output/logs/";

    public static void setVerbose(boolean verbose) {
        Logger.verbose = verbose;
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
                "WARNING", ConsoleColors.YELLOW + "WARNING" + ConsoleColors.RESET));
    }

    // Sections
    public static void newLogSection(String sectionName, String startMsg) {
        if (currentSection != null) {
            endLogSection();
        }
        logMsg(Util.getTimeWrapped() + " " + startMsg + "... ");
        currentSection = new LogSection(sectionName, System.nanoTime());
    }

    public static void endLogSection() {
        logMsgLn("Done");
        logElapsedTime("-> " + currentSection.name + " complete in: ", currentSection.startTime);
        currentSection = null;
    }

    // Time logging
    public static void logElapsedTime(String title, long startTime) {
        logElapsedTime(title, startTime, System.nanoTime());
    }

    public static void logElapsedTime(String title, long startTime, long endTime) {
        String elapsedTime = title + TimeFormatter.timeToString(endTime - startTime) + "\n";
        log.append(elapsedTime);
        if (verbose) {
            print(elapsedTime);
        }
    }

    public static void writeLogsToFile(RenderSettings settings) {
        writeLogsToFile(settings.toFilenameString() + ".txt");
    }

    public static void writeLogsToFile(String filepath) {
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
