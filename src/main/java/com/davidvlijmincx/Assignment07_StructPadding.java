package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment07_StructPadding extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        // TODO: Fix the struct layout below by adding padding where the C compiler requires it.
        //
        // The C struct is:
        //   typedef struct {
        //       char   classification;  // offset 0,  1 byte
        //       int    sector_id;       // offset 4,  4 bytes  ← compiler inserted 3 bytes of padding here
        //       double signal_strength; // offset 8,  8 bytes
        //       char   active;          // offset 16, 1 byte
        //   } SensorReading;            // sizeof = 24         ← 7 bytes of trailing padding
        //
        // The C compiler silently inserts padding so every field starts at a multiple of its own
        // size (int → multiple of 4, double → multiple of 8). FFM validates this and will throw
        // an IllegalArgumentException if the offsets don't match.
        //
        // Start by uncommenting the broken layout to see the error, then fix it.
        // Hint: MemoryLayout.paddingLayout(N) inserts N bytes of padding.
        //
        // BROKEN uncomment to see the IllegalArgumentException:
//        StructLayout sensorLayout = MemoryLayout.structLayout(
//            ValueLayout.JAVA_BYTE.withName("classification"),
//            ValueLayout.JAVA_INT.withName("sector_id"),
//            ValueLayout.JAVA_DOUBLE.withName("signal_strength"),
//            ValueLayout.JAVA_BYTE.withName("active")
//        ).withName("SensorReading");

        StructLayout sensorLayout = null; // replace with the corrected layout

        System.out.println("MOTHER: SensorReading layout size = " + sensorLayout.byteSize() + " bytes (expected 24)");

        // GIVEN
        VarHandle classificationHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("classification"));
        VarHandle sectorIdHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("sector_id"));
        VarHandle signalHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("signal_strength"));
        VarHandle activeHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("active"));

        MethodHandle scanSector = linker.downcallHandle(lookup.findOrThrow("scan_sector"), FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment reading = arena.allocate(sensorLayout);

            int targetSector = 7;
            System.out.println("MOTHER: Scanning sector " + targetSector + "...");

            scanSector.invokeExact(targetSector, reading);

            byte classification = (byte) classificationHandle.get(reading, 0L);
            int sectorId = (int) sectorIdHandle.get(reading, 0L);
            double signalStrength = (double) signalHandle.get(reading, 0L);
            byte active = (byte) activeHandle.get(reading, 0L);

            System.out.println("\n=== SECTOR SCAN REPORT ===");
            System.out.println("Classification : " + (char) classification);
            System.out.println("Sector ID      : " + sectorId);
            System.out.println("Signal Strength: " + signalStrength);
            System.out.println("Active         : " + (active == 1 ? "YES" : "NO"));

            System.out.println();
            if (active == 0) {
                System.out.println("MOTHER: Subject is currently dormant. Do not approach.");
                System.out.println("MOTHER: Proximity may trigger a hostile response. Maintain safe distance.");
            } else {
                System.out.println("MOTHER: !! WARNING SUBJECT IS ACTIVE !!");
                System.out.println("MOTHER: Recommend immediate withdrawal from sector.");
            }
        }
    }
}
