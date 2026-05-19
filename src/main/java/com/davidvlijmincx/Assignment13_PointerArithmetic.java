package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment13_PointerArithmetic extends MainframeTerminal {

    static final StructLayout ALIEN_SIGNATURE_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("id"),
            ValueLayout.JAVA_INT.withName("threat_level"),
            ValueLayout.JAVA_FLOAT.withName("direction"),
            ValueLayout.JAVA_FLOAT.withName("movement_speed")
    );

    public static void main(String[] args) throws Throwable {

        // GIVEN
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        // GIVEN C signature: AlienSignature** analyze_sensor_big_sweep(int ping_count)
        MethodHandle analyzeSensorSweep = linker.downcallHandle(
                stdlib.find("analyze_sensor_big_sweep").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
        );

        // GIVEN C signature: void free_sensor_sweep(AlienSignature**, int ping_count)
        MethodHandle freeSensorSweep = linker.downcallHandle(
                stdlib.find("free_sensor_sweep").orElseThrow(),
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
        );

        // GIVEN You will use the segment below as this holds all the data.
        int pingCount = 10;
        MemorySegment pointerArraySegment = (MemorySegment) analyzeSensorSweep.invoke(pingCount);

        System.out.println("MOTHER: Initiating full vessel sensor sweep. Processing " + pingCount + " sensor pings...\n");

        if (pointerArraySegment.equals(MemorySegment.NULL)) {
            System.out.println("MOTHER: Sensor sweep failed. All systems may be compromised.");
            return;
        }

        // TODO 1: Reinterpret the raw pointer array to its true byte size.
        // C returned an AlienSignature** pingCount pointers packed in a row.
        // Hint: each slot is ValueLayout.ADDRESS.byteSize() bytes wide.
        MemorySegment sizedPointerArray = null;

        // GIVEN VarHandles to read struct fields
        VarHandle idHandle     = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("id"));
        VarHandle threatHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("threat_level"));
        VarHandle dirHandle    = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("direction"));
        VarHandle speedHandle  = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("movement_speed"));

        // Here you chase pointers
        for (long i = 0; i < pingCount; i++) {

            // TODO 2: Read the i-th pointer from the array.
            // Hint: sizedPointerArray.getAtIndex(ValueLayout.ADDRESS, i)
            MemorySegment structPointer = null;

            // TODO 3 Standard approach: reinterpret structPointer as a bounded segment
            // the size of one struct, then read each field with its VarHandle.
            // Hint: structPointer.reinterpret(ALIEN_SIGNATURE_LAYOUT.byteSize())
            int id        = 0;
            int threat    = 0;
            float dir     = 0;
            float speed   = 0;

            // TODO 4  Zero-GC approach: instead of wrapping the pointer in a new MemorySegment,
            // pass its raw address directly to ZeroGcAlienSignature (see the class below).
            // The raw address is used as a byte offset into one global segment, no allocation per loop iteration.
            // Hint: structPointer.address() gives you the long address.
            int idFast     = 0;
            int threatFast = 0;
            float dirFast  = 0;
            float speedFast = 0;

            System.out.printf("Standard  ID: %d | Threat: %d | Dir: %.1f | Speed: %.1f%n", id, threat, dir, speed);
            System.out.printf("Zero-GC   ID: %d | Threat: %d | Dir: %.1f | Speed: %.1f%n%n", idFast, threatFast, dirFast, speedFast);
        }

        // GIVEN
        freeSensorSweep.invokeExact(pointerArraySegment, pingCount);

        System.out.println("\nMOTHER: Sensor sweep complete. Cross-referencing signatures with crew biometric registry...");
        MainframeTerminal.theEnd(pingCount);
    }
}


// GIVEN the Zero-GC helper you call in TODO 4.
// One MAX_VALUE-sized segment is anchored at address 0; a raw pointer's numeric value
// becomes the byte offset into that segment. VarHandles accept (segment, byteOffset),
// so no new MemorySegment is created per struct, just arithmetic on the pointer address.
class ZeroGcAlienSignature {
    public static final GroupLayout ALIEN_SIGNATURE_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("id"),
            ValueLayout.JAVA_INT.withName("threat_level"),
            ValueLayout.JAVA_FLOAT.withName("direction"),
            ValueLayout.JAVA_FLOAT.withName("movement_speed")
    );

    private static final MemorySegment GLOBAL_MEMORY = MemorySegment.ofAddress(0L).reinterpret(Long.MAX_VALUE);


    private static final VarHandle idHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("id"));
    private static final VarHandle threatHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("threat_level"));
    private static final VarHandle dirHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("direction"));
    private static final VarHandle speedHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("movement_speed"));

    public static int getId(long address) {
        return (int) idHandle.get(GLOBAL_MEMORY, address);
    }

    public static int getThreat(long address) {
        return (int) threatHandle.get(GLOBAL_MEMORY, address);
    }
    public static float getDir(long address) {
        return (float) dirHandle.get(GLOBAL_MEMORY, address);
    }
    public static float getSpeed(long address) {
        return (float) speedHandle.get(GLOBAL_MEMORY, address);
    }

}

