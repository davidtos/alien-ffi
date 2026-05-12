package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

public class Assignment03_Strings extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        // TODO 1: Boot up the linker and load the shared library.
        // Hint: Linker.nativeLinker() and SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto())
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        // TODO 2: Define the FunctionDescriptor.
        // get_threat_assessment takes a C int and RETURNS a pointer (const char*).
        // Hint: The return type is ValueLayout.ADDRESS
        FunctionDescriptor descriptor = FunctionDescriptor.of(ADDRESS, JAVA_INT);

        // TODO 3: Look up the symbol and create the downcall handle.
        // Hint: linker.downcallHandle(lookup.findOrThrow("get_threat_assessment"), descriptor)
        MethodHandle getThreatAssessment = linker.downcallHandle(lookup.findOrThrow("get_threat_assessment"), descriptor);

        int threatLevel = 8;

        System.out.println("MOTHER: Requesting threat assessment for level " + threatLevel + "...");

        // TODO 4: Invoke the handle.
        // Cast the result to MemorySegment.
        // Hint: (MemorySegment) getThreatAssessment.invokeExact(threatLevel)
        MemorySegment rawPointer = (MemorySegment) getThreatAssessment.invokeExact(threatLevel);

        // TODO 5: The returned segment has byteSize() == 0.
        // Java refuses to read from it until you declare how large the backing memory is.
        // The C string is null-terminated, so getString() stops at '\0' regardless of the bound.
        // Hint: rawPointer.reinterpret(256)
        MemorySegment bounded = rawPointer.reinterpret(256);


        // TODO 6: Read the null-terminated C string.
        // Hint: bounded.getString(0) decodes bytes from offset 0 until '\0'
        String assessment = bounded.getString(0);

        System.out.println("MOTHER: " + assessment);
        System.out.println("MOTHER: Recommend all crew be notified. This vessel may no longer be secure.");
    }
}
