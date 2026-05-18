package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment06_Strucs extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        Linker linker = Linker.nativeLinker();

        // TODO 1: Define the struct layout — this is the Java mirror of the C struct.
        // C struct:
        //
        //   typedef struct {
        //    int entity_id;
        //    float threat_level;
        //    long mass_kg;
        //    int produces_acid;
        //} BiomassSignature;

        //
        // Hint: MemoryLayout.structLayout(...) takes ValueLayouts as arguments.
        // Give each field a name using .withName("field_name") you'll need those names for VarHandles.
        // Give the whole GroupLayout a name too: .withName("BiomassSignature")
        GroupLayout biomassLayout = null;

        // TODO 2: Create a VarHandle for each field so you can read values out of the struct later.
        // A VarHandle is like a typed pointer to a specific field inside a MemorySegment.
        // Hint: biomassLayout.varHandle(MemoryLayout.PathElement.groupElement("field_name"))
        VarHandle entityIdHandle = null;
        VarHandle threatLevelHandle = null;
        VarHandle massHandle = null;
        VarHandle acidHandle = null;

        // GIVEN
        MethodHandle executeDeepScan = linker.downcallHandle(
                SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto()).findOrThrow("execute_deep_scan"),
                FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );

        int sectorId = 4;

        try (Arena arena = Arena.ofConfined()) {

            // TODO 3: Allocate a native memory segment sized to hold one BiomassSignature struct.
            // Hint: arena.allocate(layout) pass it the GroupLayout you defined above
            MemorySegment scanOutput = null;

            System.out.println("MOTHER: Initiating deep biomass scan of Sector " + sectorId + ". Analysing biological signatures...");

            executeDeepScan.invokeExact(sectorId, scanOutput);

            // TODO 4: Read each field from the struct using your VarHandles.
            // Hint: varHandle.get(segment, 0L) 0 is the byte offset from the start of the segment.
            // Don't forget to cast the result to the right Java type.
            int entityId = 0;
            float threatLevel = 0;
            long mass = 0;
            int producesAcid = 0;

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


