package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment02_NativeMemory extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        // TODO 1: Create a confined Arena using try-with-resources, it will free native memory when the block exits.
        try(Arena arena = Arena.ofConfined()) {
            // TODO 2: Perform a library lookup and find "get_current_stardate".
            MemorySegment getCurrentStardate = SymbolLookup.libraryLookup( getLibPath(), arena).findOrThrow("get_current_stardate");

            // TODO 3: Describe the function signature.
            FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);

            // TODO 4: Create the downcall handle.
            MethodHandle connectHandle = getLinker().downcallHandle(getCurrentStardate, descriptor);

            System.out.println("MOTHER: Retrieving mission stardate from mainframe...");

            // TODO 5: Allocate native memory for the double the C function will write into.
            MemorySegment stardate = arena.allocate(ValueLayout.JAVA_DOUBLE);

            // TODO 6: Invoke the handle, passing your allocated segment as the output pointer.
            connectHandle.invoke(stardate);

            // TODO 7: Read the double back out of the segment.
            double date = stardate.get(ValueLayout.JAVA_DOUBLE,0);

            // TODO 8: Uncomment and fill in the variable name once you have the double.
             System.out.println("MOTHER: Current stardate confirmed: " + stardateToDate(date) + ".");
            System.out.println("MOTHER: You have been in hypersleep for the duration of transit.");
            System.out.println("MOTHER: The mission clock is now running. Proceed with sector survey.");

        }

    }
}


