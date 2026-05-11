package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment05_Arrays extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try (Arena arena = Arena.ofConfined()) {
            int numberOfSectors = 3;

            MemorySegment sectorIds = arena.allocate(ValueLayout.JAVA_INT, numberOfSectors);
            sectorIds.setAtIndex(ValueLayout.JAVA_INT, 0, 5);
            sectorIds.setAtIndex(ValueLayout.JAVA_INT, 1, 6);
            sectorIds.setAtIndex(ValueLayout.JAVA_INT, 2, 7);

            MemorySegment reportsPtr = arena.allocate(ValueLayout.ADDRESS, numberOfSectors);

            // GIVEN
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS,ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
            MemorySegment getCurrentStardate = SymbolLookup.libraryLookup( getLibPath(), arena).findOrThrow("run_sector_scan");
            MethodHandle runDeepScanHandle = getLinker().downcallHandle(getCurrentStardate, descriptor);
            runDeepScanHandle.invokeExact(sectorIds, reportsPtr, numberOfSectors);
            System.out.println("--- SECTOR SCAN RESULTS ---");


            for (int i = 0; i < numberOfSectors; i++) {

                // ASSIGNMENT
                int sectorNum = sectorIds.getAtIndex(ValueLayout.JAVA_INT, i);
                MemorySegment stringAddress = reportsPtr.getAtIndex(ValueLayout.ADDRESS, i).reinterpret(200);
                String report = stringAddress.getString(0);
                System.out.println("Sector " + sectorNum + ": " + report);
            }
        }

    }
}


