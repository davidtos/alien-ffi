package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment04_Critical extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try(Arena arena = Arena.ofConfined()){
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);

            MemorySegment s = SymbolLookup.libraryLookup( getLibPath(), arena).findOrThrow("authenticate_biometrics");

            MethodHandle connectHandle = getLinker().downcallHandle(s, descriptor, Linker.Option.critical(true));

            System.out.println("MOTHER: Initiating biometric authentication. Please stand by...");

            MemorySegment memorySegment = MemorySegment.ofArray(("Ash").getBytes());

            connectHandle.invoke(memorySegment);

            System.out.println("MOTHER: Biometric confirmed. Crew member Ash logged to registry.");
            System.out.println("MOTHER: All registered designations are on file.");

        }

    }
}


