package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment03_Critical extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try(Arena arena = Arena.ofConfined()){
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);

            MemorySegment s = SymbolLookup.libraryLookup( getLibPath(), arena).findOrThrow("authenticate_biometrics");

            MethodHandle connectHandle = getLinker().downcallHandle(s, descriptor, Linker.Option.critical(true));

            MemorySegment memorySegment = MemorySegment.ofArray(("David").getBytes());

            connectHandle.invoke(memorySegment);

        }

    }
}


