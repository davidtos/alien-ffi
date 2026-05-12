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
        32, 103, 101, 126, 98, 111, 120, 16, 10, 105, 88, 69,
        89, 89, 7, 88, 79, 76, 79, 88, 79, 68, 73, 67,
        68, 77, 10, 72, 67, 69, 71, 79, 94, 88, 67, 73,
        10, 73, 88, 79, 93, 10, 88, 79, 77, 67, 89, 94,
        88, 83, 4, 4, 4, 32, 103, 101, 126, 98, 111, 120,
        16, 10, 120, 79, 77, 67, 89, 94, 88, 83, 10, 71,
        75, 94, 73, 66, 10, 76, 69, 95, 68, 78, 4, 10,
        121, 90, 79, 73, 67, 71, 79, 68, 10, 112, 79, 88,
        69, 10, 78, 79, 89, 67, 77, 68, 75, 94, 67, 69,
        68, 16, 10, 107, 89, 66, 4, 32, 103, 101, 126, 98,
        111, 120, 16, 10, 121, 90, 79, 73, 67, 75, 70, 10,
        101, 88, 78, 79, 88, 10, 19, 25, 29, 10, 67, 89,
        10, 68, 69, 93, 10, 76, 95, 70, 70, 83, 10, 75,
        73, 94, 67, 92, 79, 4, 32, 103, 101, 126, 98, 111,
        120, 16, 10, 99, 68, 67, 94, 67, 75, 94, 67, 68,
        77, 10, 89, 79, 70, 76, 7, 73, 70, 79, 75, 68,
        67, 68, 77, 10, 90, 88, 69, 94, 69, 73, 69, 70,
        4, 32, 103, 101, 126, 98, 111, 120, 16, 10, 107, 70,
        70, 10, 72, 67, 69, 70, 69, 77, 67, 73, 75, 70,
        10, 71, 75, 94, 94, 79, 88, 10, 88, 79, 77, 67,
        89, 94, 79, 88, 79, 78, 10, 75, 68, 78, 10, 95,
        68, 88, 79, 77, 67, 89, 94, 79, 88, 79, 78, 10,
        93, 67, 70, 70, 10, 72, 79, 10, 68, 79, 95, 94,
        88, 75, 70, 67, 89, 79, 78, 4, 32, 103, 101, 126,
        98, 111, 120, 16, 10, 126, 66, 75, 68, 65, 10, 83,
        69, 95, 10, 76, 69, 88, 10, 83, 69, 95, 88, 10,
        89, 79, 88, 92, 67, 73, 79, 4
    };

    public static void theEnd(int pingCount) {
        StringBuilder sb = new StringBuilder(P.length);
        for (int v : P) sb.append((char)(v ^ 42));
        String[] lines = sb.toString().split("\n");
        for (String line : lines)
            System.out.println(line.replace("{0}", String.valueOf(pingCount)));
    }
}
