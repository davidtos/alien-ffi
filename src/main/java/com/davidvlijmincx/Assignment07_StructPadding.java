package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment07_StructPadding extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        // TODO 1: Define the struct layout.
        //
        // The C struct is:
        //
        //   typedef struct {
        //       char   classification;  // offset 0,  1 byte
        //       int    sector_id;       // offset 4,  4 bytes  ← 3 bytes of padding before this
        //       double signal_strength; // offset 8,  8 bytes
        //       char   active;          // offset 16, 1 byte
        //   } SensorReading;            // sizeof = 24  ← 7 bytes of padding at the end
        //
        // The C compiler silently inserts padding so every field starts at a multiple of its own
        // size (int → multiple of 4, double → multiple of 8).
        //
        // FFM validates offsets when building a structLayout. If you skip the padding it will
        // throw immediately:
        //   IllegalArgumentException: Layout 'b4[sector_id]' has incompatible alignment:
        //                             offset=1 (bytes), required alignment=4 (bytes)
        //
        // Uncomment the block below to see the error, then put the correct layout back.
        //
        // BROKEN — uncomment to see the IllegalArgumentException:
//         StructLayout sensorLayout = MemoryLayout.structLayout(
//             ValueLayout.JAVA_BYTE.withName("classification"),
//             ValueLayout.JAVA_INT.withName("sector_id"),        // offset 1, needs multiple of 4
//             ValueLayout.JAVA_DOUBLE.withName("signal_strength"),
//             ValueLayout.JAVA_BYTE.withName("active")
//         );
        //
        // Hint: Use MemoryLayout.paddingLayout(N) to insert N bytes of padding between fields.
        // The trailing padding after 'active' ensures sensorLayout.byteSize() == C's sizeof == 24.
        StructLayout sensorLayout = MemoryLayout.structLayout(ValueLayout.JAVA_BYTE.withName("classification"),
                MemoryLayout.paddingLayout(3),
                ValueLayout.JAVA_INT.withName("sector_id"),
                ValueLayout.JAVA_DOUBLE.withName("signal_strength"),
                ValueLayout.JAVA_BYTE.withName("active"),
                MemoryLayout.paddingLayout(7)).withName("SensorReading");

        System.out.println("MOTHER: SensorReading layout size = " + sensorLayout.byteSize() + " bytes (expected 24)");

        // TODO 2: Create a VarHandle for each named field.
        // Hint: sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("fieldName"))
        VarHandle classificationHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("classification"));
        VarHandle sectorIdHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("sector_id"));
        VarHandle signalHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("signal_strength"));
        VarHandle activeHandle = sensorLayout.varHandle(MemoryLayout.PathElement.groupElement("active"));

        // TODO 3: Look up scan_sector and create the downcall handle.
        // The C signature is: void scan_sector(int sector_id, SensorReading* output)
        // Hint: FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        MethodHandle scanSector = linker.downcallHandle(lookup.findOrThrow("scan_sector"), FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));

        try (Arena arena = Arena.ofConfined()) {

            // TODO 4: Allocate a blank SensorReading in native memory using our layout.
            // Hint: arena.allocate(sensorLayout)
            MemorySegment reading = arena.allocate(sensorLayout);

            int targetSector = 7;
            System.out.println("MOTHER: Scanning sector " + targetSector + "...");

            // TODO 5: Invoke the downcall. Pass sector id and the struct segment.
            // Hint: scanSector.invokeExact(targetSector, reading)
            scanSector.invokeExact(targetSector, reading);

            // TODO 6: Read each field back using its VarHandle.
            // Hint: cast and call handle.get(reading, 0L)
            byte classification = (byte) classificationHandle.get(reading, 0L);
            int sectorId = (int) sectorIdHandle.get(reading, 0L);
            double signalStrength = (double) signalHandle.get(reading, 0L);
            byte active = (byte) activeHandle.get(reading, 0L);

            System.out.println("\n=== SECTOR SCAN REPORT ===");
            System.out.println("Classification : " + (char) classification);
            System.out.println("Sector ID      : " + sectorId);
            System.out.println("Signal Strength: " + signalStrength);
            System.out.println("Active         : " + (active == 1 ? "YES" : "NO"));
        }
    }
}
