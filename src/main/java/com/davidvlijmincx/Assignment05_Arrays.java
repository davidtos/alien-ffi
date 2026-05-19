package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment05_Arrays extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try (Arena arena = Arena.ofConfined()) {
            int numberOfSectors = 3;

            // TODO 1: Allocate a native array of 3 ints to hold the sector IDs.
            // Hint: arena.allocate(layout, count) — you've used allocate(layout) before, this variant allocates multiple elements
            MemorySegment sectorIds = null;

            // TODO 2: Write sector IDs 5, 6, 7 into the array.
            // Hint: MemorySegment has a setAtIndex(layout, index, value) method

            // TODO 3: Allocate a native array of 3 pointers — the C function will write a string pointer into each slot.
            // Hint: use ValueLayout.ADDRESS as the layout
            MemorySegment reportsPtr = null;

            // GIVEN
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT); // return Type, Array, nr of element in the array
            MemorySegment runSectorScan = SymbolLookup.libraryLookup(getLibPath(), arena).findOrThrow("run_sector_scan");
            MethodHandle runScanHandle = getLinker().downcallHandle(runSectorScan, descriptor);

            System.out.println("MOTHER: Running scheduled sector sweep across " + numberOfSectors + " sectors. Stand by...");
            runScanHandle.invokeExact(sectorIds, reportsPtr, numberOfSectors);
            System.out.println("\n--- SECTOR SCAN RESULTS ---");

            for (int i = 0; i < numberOfSectors; i++) {
                // TODO 4: Read the sector ID back out of sectorIds at index i.
                // Hint: getAtIndex(layout, index)
                int sectorNum = 0;

                // TODO 5: Read the string pointer out of reportsPtr at index i, reinterpret it, then get the String.
                MemorySegment stringAddress = null; // Get the String (remember to give the segment the correct size)
                String report = null; // read the string value from the String address

                System.out.println("Sector " + sectorNum + ": " + report);
            }

            System.out.println("\nMOTHER: Sweep complete. Flagging anomalous readings for deep analysis.");
        }

    }
}


