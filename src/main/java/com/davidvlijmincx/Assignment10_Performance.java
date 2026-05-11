package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment10_Performance extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        System.out.println("pro = " + processPing(1,5));
        System.out.println("pro = " + improvedProcessPing(1,5));

    }

    public static int improvedProcessPing(int x, int y) throws Throwable {
            throw new UnsupportedOperationException("Has to be implemented first");
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


