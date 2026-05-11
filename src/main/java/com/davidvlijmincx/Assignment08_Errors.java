package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment08_Errors extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup( getLibPath(), Arena.ofAuto());


        // 1. THE UPGRADE: Tell the Linker we want to capture the "errno" state
        Linker.Option captureErrno = Linker.Option.captureCallState("errno");

        // 2. Create the downcall. We pass our capture option at the very end.
        // NOTE: This magically adds a MemorySegment as the FIRST argument to our MethodHandle!
        MethodHandle sendBeacon = linker.downcallHandle(
                lookup.find("transmit_distress_beacon").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT),
                captureErrno
        );

        // 3. FFM provides a built-in "Blueprint" (StructLayout) for the captured state
        StructLayout stateLayout = Linker.Option.captureStateLayout();

        // 4. Calibrate the "Laser" to target the "errno" field in the state blueprint
        VarHandle errnoHandle = stateLayout.varHandle(MemoryLayout.PathElement.groupElement("errno"));

        try (Arena arena = Arena.ofConfined()) {

            // 5. Allocate native memory to hold the captured state
            MemorySegment capturedState = arena.allocate(stateLayout);

            // Let the attendees experiment with frequency bands 1, 2, and 3
            int frequencyBand = 2;

            System.out.println("[Java] Attempting to broadcast distress signal...");

            // 6. EXECUTE. We MUST pass the capturedState memory segment as the first argument!
            int result = (int) sendBeacon.invokeExact(capturedState, frequencyBand);

            // 7. Error Handling Logic
            if (result == -1) {
                System.out.println(">>> TRANSMISSION FAILED! <<<");

                // Read the captured errno from our memory segment
                int errCode = (int) errnoHandle.get(capturedState, 0L);

                if (errCode == 16) {
                    System.out.println("Mainframe Error 16: EBUSY.");
                    System.out.println("Reason: The antenna dish is jammed by alien biomass! You need to clear it manually.");
                } else if (errCode == 111) {
                    System.out.println("Mainframe Error 111: ECONNREFUSED.");
                    System.out.println("Reason: Hardline severed. The alien cut the power to the transmitter!");
                } else {
                    System.out.println("Unknown Critical Failure: " + errCode);
                }
            } else {
                System.out.println(">>> TRANSMISSION SUCCESSFUL. Hold your ground, Colonial Marines are en route. <<<");
            }
        }
    }
}


