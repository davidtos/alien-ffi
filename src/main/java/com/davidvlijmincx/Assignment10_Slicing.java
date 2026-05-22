package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment10_Slicing extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        // GIVEN
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        try (Arena arena = Arena.ofConfined()) {

            // GIVEN downcall handles (same pattern as before)
            MethodHandle getCoreDump = linker.downcallHandle(
                    lookup.find("get_core_dump").get(),
                    FunctionDescriptor.of(ValueLayout.ADDRESS)
            );

            MethodHandle decryptDirective = linker.downcallHandle(
                    lookup.find("decrypt_dump").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
            );

            // GIVEN call get_core_dump. FFM returns a 0-length segment because it doesn't know the C array size
            MemorySegment rawPointer = (MemorySegment) getCoreDump.invokeExact();

            // TODO 1: Reinterpret rawPointer to give it a known size of 10240 bytes.
            long dumpSize = 10240;
            MemorySegment fullCoreDump = rawPointer.reinterpret(dumpSize);

            // TODO 2: Slice the segment extract 256 bytes starting at offset 4096.
            long secretOffset = 4096;
            int secretLength = 256;

            System.out.println("MOTHER: Accessing classified memory sector. Isolating offset " + secretOffset + "...");
            System.out.println("MOTHER: Decrypting directive. Stand by...\n");

            // TODO 2.1 holder for the slice.
            MemorySegment directiveSlice = fullCoreDump.asSlice(secretOffset, secretLength);

            decryptDirective.invokeExact(directiveSlice, secretLength);

        } catch (Throwable t) {
            System.err.println("MOTHER: Decryption failed. Classified directive remains sealed.");
            t.printStackTrace();
        }
    }
}


