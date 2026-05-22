package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment01_Downcall extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        // TODO 1: Get a Linker, it understands how to call native code on this platform.
        Linker linker = Linker.nativeLinker();

        // TODO 2: Load the ship's library so you can look up functions in it.
        String libPath = getLibPath();
        SymbolLookup shipSystems = SymbolLookup.libraryLookup( libPath, Arena.ofAuto());

        // TODO 3: Find the function "connect_to_mainframe" in the lookup.
        MemorySegment connectToMainframe = shipSystems.findOrThrow("connect_to_mainframe");

        // TODO 4: Describe the C function's signature so the linker knows how to call it.
        FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);


        // TODO 5: Ask the linker to create a MethodHandle from the symbol and descriptor.
        MethodHandle connectHandle = linker.downcallHandle(connectToMainframe, descriptor);

        System.out.println("MOTHER: Channel open. Transmitting crew access code...");

        // TODO 6: Invoke the handle with access code 42 and capture the int result.
        int result = (int) connectHandle.invokeExact(42);

        if (result == 200) {
            System.out.println("MOTHER: Access granted. Welcome back. The ship has been waiting.");
        } else {
            System.err.println("MOTHER: ERROR. Handshake failed with code: " + result + ". Aborting mission link.");
        }
    }
}




