package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment07_StructPadding extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        // TODO: Fix the struct layout below by adding padding where the C compiler requires it.
        StructLayout sensorLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_BYTE.withName("classification"),
                MemoryLayout.paddingLayout(3),
                ValueLayout.JAVA_INT.withName("sector_id"),
                ValueLayout.JAVA_DOUBLE.withName("signal_strength"),
                ValueLayout.JAVA_BYTE.withName("active"),
                MemoryLayout.paddingLayout(7)
        ).withName("SensorReading");

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
