package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment02_NativeMemory extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        // TODO 1: Create a confined Arena using try-with-resources, it will free native memory when the block exits.

        // TODO 2: Perform a library lookup and find "get_current_stardate".

        // TODO 3: Describe the function signature.
        // C signature: void get_current_stardate(double *out)
        // Hint: the function returns void and takes a pointer, look at FunctionDescriptor.ofVoid() and ValueLayout.ADDRESS

        // TODO 4: Create the downcall handle.

        System.out.println("MOTHER: Retrieving mission stardate from mainframe...");

        // TODO 5: Allocate native memory for the double the C function will write into.
        // Hint: arena.allocate(...) pass it a ValueLayout that matches the type of the pointer

        // TODO 6: Invoke the handle, passing your allocated segment as the output pointer.

        // TODO 7: Read the double back out of the segment.
        // Hint: MemorySegment.get(layout, offset)  offset 0 means the start of the segment

        // TODO 8: Uncomment and fill in the variable name once you have the double.
        // System.out.println("MOTHER: Current stardate confirmed: " + stardateToDate(???) + ".");
        System.out.println("MOTHER: You have been in hypersleep for the duration of transit.");
        System.out.println("MOTHER: The mission clock is now running. Proceed with sector survey.");

    }
}


