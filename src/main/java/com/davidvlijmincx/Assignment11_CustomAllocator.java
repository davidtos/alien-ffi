package com.davidvlijmincx;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

public class Assignment11_CustomAllocator extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        System.out.println("MOTHER: Alien presence confirmed in multiple sectors. Initiating emergency containment.");
        System.out.println("MOTHER: Establishing Safe Zone Alpha. Allocating emergency memory grid...\n");

        try (Arena arena = Arena.ofConfined()) {

            // 1. Allocate a 1MB Safe Zone using standard allocation
            MemorySegment sector4 = arena.allocate(1024 * 1024);

            // 2. Wrap it in the custom allocator
            SafeZoneAllocator safeAllocator = new SafeZoneAllocator(sector4);

            // 3. Use the custom allocator for all further operations
            // (e.g., allocating strings or structs to send to C)
            MemorySegment commandStr = safeAllocator.allocateFrom("SEAL DOORS");
            MemorySegment overrideCode = safeAllocator.allocateFrom(ValueLayout.JAVA_INT, 937);

            System.out.println("\nMOTHER: Command issued — " + commandStr.getString(0) + ".");
            System.out.println("MOTHER: Override code " + overrideCode.get(ValueLayout.JAVA_INT, 0) + " is active.");
            System.out.println("MOTHER: All internal bulkhead doors are sealed. Safe Zone is holding.");
        }
    }
}

class SafeZoneAllocator implements SegmentAllocator {

    // GIVEN — backing segment and bump pointer
    private final MemorySegment safeZone;
    private long currentOffset = 0;

    public SafeZoneAllocator(MemorySegment safeZone) {
        this.safeZone = safeZone;
    }

    // The SegmentAllocator interface requires you to implement this one method.
    // It is a bump-pointer allocator: track an offset into safeZone,
    // advance it on every allocation, and hand back a slice.
    @Override
    public MemorySegment allocate(long byteSize, long byteAlignment) {

        // TODO 1: Advance currentOffset to the next multiple of byteAlignment.
        // Native types (int, double, structs) must start at an aligned address —
        // if currentOffset isn't already aligned, insert padding bytes.
        // Hint: remainder = currentOffset % byteAlignment
        //       padding   = (remainder == 0) ? 0 : (byteAlignment - remainder)

        // TODO 2: Check capacity, throw OutOfMemoryError if the allocation
        // would exceed safeZone.byteSize().

        // TODO 3: Slice byteSize bytes from safeZone at currentOffset.
        // Hint: safeZone.asSlice(currentOffset, byteSize) like you used asSlice in Assignment 10.
        MemorySegment allocatedSlice = null;

        // TODO 4: Advance currentOffset by byteSize so the next allocation starts after this one.

        System.out.println("MU-TH-UR: " + byteSize + " bytes allocated in Safe Zone at offset " + (currentOffset - byteSize));

        return allocatedSlice;
    }
}


