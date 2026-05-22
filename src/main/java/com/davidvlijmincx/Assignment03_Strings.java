package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

public class Assignment03_Strings extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        // TODO 1: Get the linker and load the shared library.
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        // TODO 2: Define the FunctionDescriptor for "get_threat_assessment".
        FunctionDescriptor descriptor = FunctionDescriptor.of(ADDRESS, JAVA_INT);

        // TODO 3: Look up the symbol and create the downcall handle.
        MethodHandle getThreatAssessment = linker.downcallHandle(lookup.findOrThrow("get_threat_assessment"), descriptor);

        int threatLevel = 8;

        System.out.println("MOTHER: Requesting threat assessment for level " + threatLevel + "...");

        // TODO 4: Invoke the handle and capture the result as a MemorySegment.
        MemorySegment rawPointer = (MemorySegment) getThreatAssessment.invokeExact(threatLevel);

        // TODO 5: The returned pointer has no known size and Java won't let you read it yet.
        MemorySegment bounded = rawPointer.reinterpret(256);

        // TODO 6: Read the null-terminated C string from the bounded segment.
        String assessment = bounded.getString(0);

        System.out.println("MOTHER: " + assessment);
        System.out.println("MOTHER: Recommend all crew be notified. This vessel may no longer be secure.");
    }
}
