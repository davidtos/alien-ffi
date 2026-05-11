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
}
