package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment06_Strucs extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        // 1. Boot up the Mainframe link
        Linker linker = Linker.nativeLinker();
        // 2. Define the Blueprint (GroupLayout)
        // NOTE: We assign .withName() to both the individual layouts AND the whole group.
        GroupLayout biomassLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("entity_id"),
                ValueLayout.JAVA_FLOAT.withName("threat_level"),
                ValueLayout.JAVA_LONG.withName("mass_kg"),
                ValueLayout.JAVA_INT.withName("produces_acid")
        ).withName("BiomassSignature");

        // 3. Calibrate the Extraction Lasers (VarHandles)
        // -> GIVE THEM THIS ONE:
        VarHandle entityIdHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("entity_id"));

        // -> MAKE THEM WRITE THESE:
        VarHandle threatLevelHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("threat_level"));
        VarHandle massHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("mass_kg"));
        VarHandle acidHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("produces_acid"));

        // 4. Locate the C Function
        MethodHandle executeDeepScan = linker.downcallHandle(
                SymbolLookup.libraryLookup( getLibPath(), Arena.ofAuto()).findOrThrow("execute_deep_scan"),
                FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );

        int sectorId = 4; // Carried over from their array assignment

        // 5. Open the Memory Arena (Containment Field)
        try (Arena arena = Arena.ofConfined()) {

            // Allocate the blank struct in native memory based on our blueprint
            MemorySegment scanOutput = arena.allocate(biomassLayout);

            System.out.println("MOTHER: Initiating deep biomass scan of Sector " + sectorId + ". Analysing biological signatures...");

            // Execute the downcall. We pass the memory address of our struct!
            executeDeepScan.invokeExact(sectorId, scanOutput);

            // 6. Extract the data using the VarHandles
            // We pass the memory segment, and a byte offset of 0L (start at the beginning of the struct)
            int entityId = (int) entityIdHandle.get(scanOutput, 0L);
            float threatLevel = (float) threatLevelHandle.get(scanOutput, 0L);
            long mass = (long) massHandle.get(scanOutput, 0L);
            int producesAcid = (int) acidHandle.get(scanOutput, 0L);

            // 7. Survival Report
            System.out.println("\n=== DEEP SCAN TELEMETRY ===");
            System.out.println("Subject ID: " + entityId);
            System.out.println("Estimated Mass: " + mass + " kg");
            System.out.println("Threat Level: " + threatLevel + "%");

            if (producesAcid == 1) {
                System.out.println("!!! CRITICAL WARNING: BIOLOGICAL ACID DETECTED. DO NOT ENGAGE !!!");
                System.out.println("MOTHER: Specimen is viable. Containment protocol is now active.");
            } else {
                System.out.println("MOTHER: No acid signature detected. Standard observation protocols apply.");
            }
        }
    }
}


