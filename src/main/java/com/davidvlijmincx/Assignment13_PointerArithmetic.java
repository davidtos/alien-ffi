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

        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = SymbolLookup.libraryLookup( getLibPath(), Arena.ofAuto());

        // 1. Map the analyze function (returns a pointer)
        MethodHandle analyzeSensorSweep = linker.downcallHandle(
                stdlib.find("analyze_sensor_big_sweep").orElseThrow(),
                FunctionDescriptor.of(
                        ValueLayout.ADDRESS, // Return type: AlienSignature**
                        ValueLayout.JAVA_INT // Input: int ping_count
                )
        );

        // 2. Map the free function
        MethodHandle freeSensorSweep = linker.downcallHandle(
                stdlib.find("free_sensor_sweep").orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS, // Input: AlienSignature**
                        ValueLayout.JAVA_INT // Input: int ping_count
                )
        );

        int pingCount = 10;

        MemorySegment pointerArraySegment = null;


            // 3. Call C: We get a zero-length memory segment representing the AlienSignature**
            pointerArraySegment = (MemorySegment) analyzeSensorSweep.invoke(pingCount);

            System.out.println("MOTHER: Initiating full vessel sensor sweep. Processing " + pingCount + " sensor pings...\n");

            if (pointerArraySegment.equals(MemorySegment.NULL)) {
                System.out.println("MOTHER: Sensor sweep failed. All systems may be compromised.");
                return;
            }

            // 4. Reinterpret the top-level array so Java knows how long it is
            long arrayByteSize = (long) pingCount * ValueLayout.ADDRESS.byteSize();
            MemorySegment sizedPointerArray = pointerArraySegment.reinterpret(arrayByteSize);

            VarHandle idHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("id"));
            VarHandle threatHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("threat_level"));
            VarHandle dirHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("direction"));
            VarHandle speedHandle = ALIEN_SIGNATURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("movement_speed"));

            // 5. Chase the pointers
            for (long i = 0; i < pingCount; i++) {
                // Read the pointer (memory address) at the current index
                MemorySegment structPointer = sizedPointerArray.getAtIndex(ValueLayout.ADDRESS, i);

                // Standard approach: reinterpret the pointer into a bounded segment, then
                // read fields with VarHandles. This creates a new MemorySegment object per iteration.
                //   MemorySegment structSegment = structPointer.reinterpret(ALIEN_SIGNATURE_LAYOUT.byteSize());
                //   int id = (int) idHandle.get(structSegment, 0L);
                //   int threat = (int) threatHandle.get(structSegment, 0L);
                //   float direction = (float) dirHandle.get(structSegment, 0L);
                //   float speed = (float) speedHandle.get(structSegment, 0L);

                // Zero-GC approach: pass the raw address to a VarHandle that operates on a
                // single global MAX_VALUE-sized segment. No per-iteration object allocation.
                int id = ZeroGcAlienSignature.getId(structPointer.address());
                int threat = ZeroGcAlienSignature.getThreat(structPointer.address());
                float direction = ZeroGcAlienSignature.getDir(structPointer.address());
                float speed = ZeroGcAlienSignature.getSpeed(structPointer.address());

                System.out.printf("Target ID: %d | Threat: %d | Dir: %.1f deg | Speed: %.1f m/s%n",
                        id, threat, direction, speed);
            }

            freeSensorSweep.invokeExact(pointerArraySegment, pingCount);


        System.out.println("\nMOTHER: Sensor sweep complete. Cross-referencing signatures with crew biometric registry...");
           MainframeTerminal.theEnd(pingCount);
    }
}


// Zero-GC reader for AlienSignature structs.
// Instead of creating a new MemorySegment per struct (which allocates a heap object),
// we maintain one MAX_VALUE-sized segment anchored at address 0. VarHandles accept a
// (segment, byteOffset) pair, so we pass GLOBAL_MEMORY + the raw pointer address.
// The JIT can eliminate the VarHandle dispatch entirely, leaving just a native memory load.
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

