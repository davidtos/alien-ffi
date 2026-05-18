package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment01_Downcall extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        // TODO 1: Get a Linker, it understands how to call native code on this platform.
        // Hint: Linker.nativeLinker()

        // TODO 2: Load the ship's library so you can look up functions in it.
        // Hint: SymbolLookup.libraryLookup(path, arena) — you'll also need an Arena for library's lifetime.
        String libPath = getLibPath();
        SymbolLookup shipSystems = null;

        // TODO 3: Find the function "connect_to_mainframe" in the lookup.
        // Hint: look for a findOrThrow method on SymbolLookup.
        MemorySegment connectToMainframe = null;

        // TODO 4: Describe the C function's signature so the linker knows how to call it.
        // C signature: int connect_to_mainframe(int code)
        // Hint: FunctionDescriptor.of(returnType, paramTypes...) — map C int to ValueLayout.JAVA_INT
        FunctionDescriptor descriptor = null;

        // TODO 5: Ask the linker to create a MethodHandle from the symbol and descriptor.
        // Hint: linker.downcallHandle(...)

        System.out.println("MOTHER: Channel open. Transmitting crew access code...");

        // TODO 6: Invoke the handle with access code 42 and capture the int result.
        // Hint: invokeExact must match the descriptor signature exactly — cast the return value.
        int result = 0; // replace with your invocation

        if (result == 200) {
            System.out.println("MOTHER: Access granted. Welcome back. The ship has been waiting.");
        } else {
            System.err.println("MOTHER: ERROR. Handshake failed with code: " + result + ". Aborting mission link.");
        }
    }
}




