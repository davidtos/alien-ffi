package com.davidvlijmincx;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

public class Assignment11_CustomAllocator extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        try (Arena arena = Arena.ofConfined()) {

            // 1. Allocate a 1MB Safe Zone using standard allocation
            MemorySegment sector4 = arena.allocate(1024 * 1024);

            // 2. Wrap it in the custom allocator
            SafeZoneAllocator safeAllocator = new SafeZoneAllocator(sector4);

            // 3. Use the custom allocator for all further operations
            // (e.g., allocating strings or structs to send to C)
            MemorySegment commandStr = safeAllocator.allocateFrom("SEAL DOORS");
            MemorySegment overrideCode = safeAllocator.allocate(ValueLayout.JAVA_INT, 937);

        }
    }
}

class SafeZoneAllocator implements SegmentAllocator {

    private final MemorySegment safeZone;
    private long currentOffset = 0;

    public SafeZoneAllocator(MemorySegment safeZone) {
        this.safeZone = safeZone;
    }

    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {
        // 1. Calculate alignment padding. C structs often require
        // memory addresses to be multiples of 4 or 8 bytes.
        long remainder = currentOffset % byteAlignment;
        long padding = (remainder == 0) ? 0 : (byteAlignment - remainder);

        currentOffset += padding;

        // 2. Check if we have breached the safe zone capacity
        if (currentOffset + byteSize > safeZone.byteSize()) {
            throw new OutOfMemoryError("CRITICAL: Safe zone memory depleted. Acid breach imminent.");
        }

        // 3. Slice the requested memory from the safe zone
        MemorySegment allocatedSlice = safeZone.asSlice(currentOffset, byteSize);

        // 4. Move the bump pointer forward
        currentOffset += byteSize;

        // Optional: Console output for the story vibe
         System.out.println("MU-TH-UR: " + byteSize + " bytes allocated in Safe Zone at offset " + (currentOffset - byteSize));

        return allocatedSlice;
    }
}


