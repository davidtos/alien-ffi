package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment04_Critical extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try(Arena arena = Arena.ofConfined()){
            // TODO 1: Load the library and find "authenticate_biometrics".
            // C signature: void authenticate_biometrics(const char *name)

            // TODO 2: Create the FunctionDescriptor and downcall handle.
            // This time, pass a Linker.Option as a third argument to downcallHandle.
            //
            // Linker.Option.critical(true) tells the JVM this call is short-lived and
            // time-sensitive. The JVM skips inserting a GC safepoint for the duration,
            // which avoids pinning the carrier thread and reduces latency.
            // Only use it for calls guaranteed to return quickly never for blocking I/O.
            // You would use this for cases were the call is very fast and memory allocation would hold you back.

            System.out.println("MOTHER: Initiating biometric authentication. Please stand by...");

            // TODO 3: Wrap the crew member's name "Ash" as a MemorySegment to pass to C.
            // Hint: MemorySegment.ofArray() can wrap a Java byte array (String has a to byte array function)

            // TODO 4: Invoke the handle.

            System.out.println("MOTHER: Biometric confirmed. Crew member Ash logged to registry.");
            System.out.println("MOTHER: All registered designations are on file.");
        }

    }
}


