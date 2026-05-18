package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment04_Critical extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try(Arena arena = Arena.ofConfined()){
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);

            MemorySegment s = SymbolLookup.libraryLookup( getLibPath(), arena).findOrThrow("authenticate_biometrics");

            // Linker.Option.critical(true) tells the JVM that this call is short and
            // time-sensitive. The JVM skips inserting a GC safepoint for the duration,
            // which avoids pinning the carrier thread and reduces latency.
            // Only use for calls that are guaranteed to return quickly.
            MethodHandle connectHandle = getLinker().downcallHandle(s, descriptor, Linker.Option.critical(true));

            System.out.println("MOTHER: Initiating biometric authentication. Please stand by...");

            MemorySegment memorySegment = MemorySegment.ofArray(("Ash").getBytes());

            connectHandle.invoke(memorySegment);

            System.out.println("MOTHER: Biometric confirmed. Crew member Ash logged to registry.");
            System.out.println("MOTHER: All registered designations are on file.");

        }

    }
}


