package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment02_NativeMemory extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        try(Arena arena = Arena.ofConfined()){
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);

            MemorySegment getCurrentStardate = SymbolLookup.libraryLookup( getLibPath(), arena).findOrThrow("get_current_stardate");

            MethodHandle connectHandle = getLinker().downcallHandle(getCurrentStardate, descriptor);

            MemorySegment stardate = arena.allocate(ValueLayout.JAVA_DOUBLE);
            connectHandle.invoke(stardate);

            double date = stardate.get(ValueLayout.JAVA_DOUBLE,0);

            System.out.println("date = " + stardateToDate(date));
        }

    }
}


