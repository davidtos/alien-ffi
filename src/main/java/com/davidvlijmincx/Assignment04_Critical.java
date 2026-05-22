package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment04_Critical extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try(Arena arena = Arena.ofConfined()){
            // TODO 1: Load the library and find "authenticate_biometrics".
            MemorySegment s = SymbolLookup.libraryLookup( getLibPath(), arena).findOrThrow("authenticate_biometrics");
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);


            // TODO 2: Create the FunctionDescriptor and downcall handle.
            MethodHandle connectHandle = getLinker().downcallHandle(s, descriptor, Linker.Option.critical(true));

            System.out.println("MOTHER: Initiating biometric authentication. Please stand by...");

            // TODO 3: Wrap the crew member's name "Ash" as a MemorySegment to pass to C.
            MemorySegment memorySegment = MemorySegment.ofArray(("Ash").getBytes());

            // TODO 4: Invoke the handle.
            connectHandle.invoke(memorySegment);

            System.out.println("MOTHER: Biometric confirmed. Crew member Ash logged to registry.");
            System.out.println("MOTHER: All registered designations are on file.");
        }

    }
}


