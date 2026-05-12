package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Assignment10_Slicing extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup( getLibPath(), Arena.ofAuto());

        try (Arena arena = Arena.ofConfined()) {

            // 1. Look up the native functions
            MethodHandle getCoreDump = linker.downcallHandle(
                    lookup.find("get_core_dump").get(),
                    FunctionDescriptor.of(ValueLayout.ADDRESS)
            );

            MethodHandle decryptDirective = linker.downcallHandle(
                    lookup.find("decrypt_dump").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
            );

            // 2. Get the core dump pointer
            // Note: FFM returns this as a 0-length MemorySegment because it doesn't know the C array size.
            MemorySegment rawPointer = (MemorySegment) getCoreDump.invokeExact();

            // 3. Reinterpret the pointer to the known dump size (10240 bytes)
            long dumpSize = 10240;
            MemorySegment fullCoreDump = rawPointer.reinterpret(dumpSize);

            // 4. Slice the segment
            long secretOffset = 4096;
            int secretLength = 256;

            System.out.println("MOTHER: Accessing classified memory sector. Isolating offset " + secretOffset + "...");

            // This creates a new view of the memory without copying the underlying data
            MemorySegment directiveSlice = fullCoreDump.asSlice(secretOffset, secretLength);

            // 5. Pass the slice to the decryption downcall — the directive is embedded in native memory
            System.out.println("MOTHER: Decrypting directive. Stand by...\n");
            decryptDirective.invokeExact(directiveSlice, secretLength);


        } catch (Throwable t) {
            System.err.println("MOTHER: Decryption failed. Classified directive remains sealed.");
            t.printStackTrace();
        }
    }
}


