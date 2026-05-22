package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment06_Strucs extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        Linker linker = Linker.nativeLinker();

        // TODO 1: Define the struct layout this is the Java mirror of the C struct.
        // C struct:
        //
        //   typedef struct {
        //    int entity_id;
        //    float threat_level;
        //    long mass_kg;
        //    int produces_acid;
        //} BiomassSignature;

        GroupLayout biomassLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("entity_id"),
                ValueLayout.JAVA_FLOAT.withName("threat_level"),
                ValueLayout.JAVA_LONG.withName("mass_kg"),
                ValueLayout.JAVA_INT.withName("produces_acid")
        ).withName("BiomassSignature");

        // TODO 2: Create a VarHandle for each field so you can read values out of the struct later.
        VarHandle entityIdHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("entity_id"));
        VarHandle threatLevelHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("threat_level"));
        VarHandle massHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("mass_kg"));
        VarHandle acidHandle = biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("produces_acid"));


        // GIVEN
        MethodHandle executeDeepScan = linker.downcallHandle(
                SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto()).findOrThrow("execute_deep_scan"),
                FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );

        int sectorId = 4;

        try (Arena arena = Arena.ofConfined()) {

            // TODO 3: Allocate a native memory segment sized to hold one BiomassSignature struct.
            MemorySegment scanOutput = arena.allocate(biomassLayout);

            System.out.println("MOTHER: Initiating deep biomass scan of Sector " + sectorId + ". Analysing biological signatures...");

            executeDeepScan.invokeExact(sectorId, scanOutput);

            // TODO 4: Read each field from the struct using your VarHandles.
            int entityId = (int) entityIdHandle.get(scanOutput, 0L);
            float threatLevel = (float) threatLevelHandle.get(scanOutput, 0L);
            long mass = (long) massHandle.get(scanOutput, 0L);
            int producesAcid = (int) acidHandle.get(scanOutput, 0L);

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


