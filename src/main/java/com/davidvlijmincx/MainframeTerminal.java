package com.davidvlijmincx;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.time.LocalDate;
import java.time.LocalDateTime;

abstract class MainframeTerminal {


    public static String getLibPath(){
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        String libDir;
        String libName;

        if (os.contains("win")) {
            libDir = "win-x86_64";
            libName = "mainframe.dll";
        } else if (os.contains("mac")) {
            libDir = arch.equals("aarch64") ? "mac-aarch64" : "mac-x86_64";
            libName = "libmainframe.dylib";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            libDir = "linux-x86_64";
            libName = "libmainframe.so";
        } else {
            throw new UnsupportedOperationException("Unknown OS/Arch: " + os + " / " + arch);
        }

        return "./libs/" + libDir + "/" + libName;
    }


    public static Linker getLinker(){
        return Linker.nativeLinker();
    }


    public static LocalDate stardateToDate(double stardate) {
        // 1. Determine the Year
        int year = (int) (stardate / 1000) + 2000;

        // 2. Determine the progress through that year (0.0 to 1.0)
        double yearFraction = (stardate % 1000) / 1000.0;

        // 3. Handle Leap Years
        boolean isLeap = LocalDate.of(year, 1, 1).isLeapYear();
        double totalDaysInYear = isLeap ? 366.0 : 365.0;

        // 4. Calculate the total seconds passed in the year
        // (Day of year * seconds in a day)
        long totalSecondsInYear = (long) (yearFraction * totalDaysInYear * 24 * 60 * 60);

        // 5. Construct the date starting from Jan 1st of that year
        return LocalDateTime.of(year, 1, 1, 0, 0)
                .plusSeconds(totalSecondsInYear).toLocalDate();
    }


    private static final int[] P = {
        32, 113, 103, 101, 126, 98, 111, 120, 10, 8254, 10, 122,
        120, 99, 101, 120, 99, 126, 115, 10, 107, 102, 122, 98,
        107, 10, 101, 124, 111, 120, 120, 99, 110, 111, 119, 32,
        103, 101, 126, 98, 111, 120, 16, 10, 81, 26, 87, 10,
        66, 69, 89, 94, 67, 70, 79, 10, 89, 67, 77, 68,
        75, 94, 95, 88, 79, 89, 10, 73, 69, 68, 76, 67,
        88, 71, 79, 78, 10, 75, 72, 69, 75, 88, 78, 10,
        94, 66, 67, 89, 10, 92, 79, 89, 89, 79, 70, 4,
        32, 103, 101, 126, 98, 111, 120, 16, 10, 107, 89, 89,
        79, 94, 10, 73, 69, 68, 76, 67, 88, 71, 79, 78,
        4, 10, 105, 88, 79, 93, 10, 71, 79, 71, 72, 79,
        88, 10, 107, 89, 66, 16, 10, 121, 90, 79, 73, 67,
        75, 70, 10, 101, 88, 78, 79, 88, 10, 19, 25, 29,
        10, 75, 73, 94, 67, 92, 79, 4, 32, 103, 101, 126,
        98, 111, 120, 16, 10, 122, 88, 67, 71, 75, 88, 83,
        10, 69, 72, 64, 79, 73, 94, 67, 92, 79, 16, 10,
        88, 79, 94, 95, 88, 68, 10, 121, 90, 79, 73, 67,
        71, 79, 68, 10, 112, 79, 88, 69, 10, 94, 69, 10,
        125, 79, 83, 70, 75, 68, 78, 7, 115, 95, 94, 75,
        68, 67, 10, 67, 68, 94, 75, 73, 94, 4, 32, 103,
        101, 126, 98, 111, 120, 16, 10, 126, 66, 67, 89, 10,
        73, 69, 71, 71, 95, 68, 67, 73, 75, 94, 67, 69,
        68, 10, 67, 89, 10, 73, 70, 75, 89, 89, 67, 76,
        67, 79, 78, 4, 10, 105, 88, 79, 93, 10, 75, 73,
        73, 79, 89, 89, 10, 66, 75, 89, 10, 72, 79, 79,
        68, 10, 88, 79, 92, 69, 65, 79, 78, 4, 32, 103,
        101, 126, 98, 111, 120, 16, 10, 125, 79, 70, 70, 10,
        78, 69, 68, 79, 6, 10, 107, 89, 66, 4, 10, 126,
        66, 79, 10, 105, 69, 71, 90, 75, 68, 83, 10, 93,
        67, 70, 70, 10, 72, 79, 10, 67, 68, 10, 94, 69,
        95, 73, 66, 4,
    };


    // Spoilers
    public static void theEnd(int pingCount) {
        StringBuilder sb = new StringBuilder(P.length);
        for (int v : P) sb.append((char)(v ^ 42));
        String[] lines = sb.toString().split("\n");
        for (String line : lines)
            System.out.println(line.replace("{0}", String.valueOf(pingCount)));
    }
}
