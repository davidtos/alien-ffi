package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment12_Performance extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        System.out.println("MOTHER: WARNING: Sensor array is receiving thousands of pings per second.");
        System.out.println("MOTHER: Current implementation cannot keep pace. Multiple contacts are closing.");
        System.out.println("MOTHER: Optimise the ping processor before they breach the bulkhead.\n");

        System.out.println("[Unoptimised] Result: " + processPing(1, 5));
        System.out.println("[Optimised]   Result: " + improvedProcessPing(1, 5));

    }

    // TODO 1: Declare static final fields for the Linker, MethodHandle, SequenceLayout, and VarHandle
    // so they are initialised once and reused on every call.
    // (Look at processPing, those four objects are recreated each time. Move them here.)


    public static int improvedProcessPing(int x, int y) throws Throwable {
        // TODO 2: Replace the per-call arena allocation with MemorySegment.ofArray().
        // This wraps a Java int[] as a MemorySegment on the heap so you have no arena and no native allocation.
        // Hint: MemorySegment.ofArray(new int[]{x, y})
        MemorySegment array = null;

        // TODO 3: Write x and y into the array using the static VarHandle, then invoke the static
        // MethodHandle and return the int result.
        // (The call itself is identical to processPing. only where the objects come from changes.)
        throw new UnsupportedOperationException("Implement the optimised version here.");
    }


    public static int processPing(int x, int y) {
        // MISTAKE 1: Re-looking up the symbol and recreating the MethodHandle
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup( getLibPath(), Arena.ofAuto());
        MethodHandle analyzeSweep = linker.downcallHandle(
                lookup.find("analyze_sensor_sweep").get(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
        );

        // MISTAKE 2: Re-creating the SequenceLayout and VarHandle
        SequenceLayout layout = MemoryLayout.sequenceLayout(2, ValueLayout.JAVA_INT);
        VarHandle intHandle = layout.varHandle(MemoryLayout.PathElement.sequenceElement());

        // MISTAKE 3: Opening an Arena and allocating memory for a single 8-byte ping
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment array = arena.allocate(layout);

            // Writing data
            intHandle.set(array, 0, 0, x);
            intHandle.set(array, 0, 1, y);

            // Downcall
            return (int) analyzeSweep.invokeExact(array, 1);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }


}


