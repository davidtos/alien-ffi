package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment01_Downcall extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        // TODO 1: Initialize the Universal Translator.
        // Hint: Use Linker.nativeLinker()
        Linker linker = Linker.nativeLinker();

        // TODO 2: Load the Ship's Directory.
        // We provided the OS-specific path for you.
        // Hint: Use SymbolLookup.libraryLookup(path, Arena.global())
        String libPath = getLibPath();
        SymbolLookup shipSystems = SymbolLookup.libraryLookup( libPath, Arena.ofAuto());

        // TODO 3: Find the specific system.
        // Hint: Use shipSystems.find("ping_mainframe").orElseThrow()
        MemorySegment connectToMainframe = shipSystems.findOrThrow("connect_to_mainframe");

        // TODO 4: Define the Protocol.
        // The C function takes a C int and returns a C int.
        // Hint: FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT)
        FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

        // TODO 5: Create the Comms Channel.
        // Hint: linker.downcallHandle(...)
        MethodHandle connectHandle = linker.downcallHandle(connectToMainframe, descriptor);

        System.out.println("MOTHER: Channel open. Sending access code 42...");

        // TODO 6: Invoke the handle! Pass the integer 42.
        // Cast the result to an int.
        int result = (int) connectHandle.invokeExact(42);

        if (result == 200) {
            System.out.println("MOTHER: Access Granted. Handshake complete.");
        } else {
            System.err.println("MOTHER: ERROR. Handshake failed with code: " + result);
        }
    }
}




