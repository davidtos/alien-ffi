package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

public class Assignment03_Strings extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        // TODO 1: Get the linker and load the shared library.

        // TODO 2: Define the FunctionDescriptor for "get_threat_assessment".
        // C signature: const char* get_threat_assessment(int level)
        // Hint: a C pointer maps to ValueLayout.ADDRESS

        // TODO 3: Look up the symbol and create the downcall handle.

        int threatLevel = 8;

        System.out.println("MOTHER: Requesting threat assessment for level " + threatLevel + "...");

        // TODO 4: Invoke the handle and capture the result as a MemorySegment.

        // TODO 5: The returned pointer has no known size and Java won't let you read it yet.
        // You need to declare a bound before you can access its contents.
        // Hint: MemorySegment.reinterpret(size) returns a new segment with a declared byte size like 256 for example.

        // TODO 6: Read the null-terminated C string from the bounded segment.
        // Hint: there's a getString method on MemorySegment

      //  System.out.println("MOTHER: " + ???);
        System.out.println("MOTHER: Recommend all crew be notified. This vessel may no longer be secure.");
    }
}
