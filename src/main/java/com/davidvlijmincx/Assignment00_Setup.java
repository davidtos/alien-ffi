package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

public class Assignment00_Setup {

    static void main() throws Throwable {

        // TODO: Run the following class
        // If everything is set up okay, you can continue to the first real assignment!

        try(var arena = Arena.ofConfined()){
            Linker linker = Linker.nativeLinker();
            MemorySegment printfSymbol = linker.defaultLookup().findOrThrow("strlen");
            MethodHandle printf = linker.downcallHandle(printfSymbol, FunctionDescriptor.of(JAVA_INT, ADDRESS));
            int value =  (int) printf.invokeExact(arena.allocateFrom("Hello, Workshop!"));
            System.out.println(value == 16
                    ? "MOTHER: Uplink confirmed. All systems nominal. You are cleared to proceed, crew member."
                    : "MOTHER: Uplink failure. Check system configuration before continuing.");
        }
    }
}
