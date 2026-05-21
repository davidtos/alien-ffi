package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public class Assignment08_Errors extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {

        // GIVEN
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        // TODO 1: Create a Linker.Option that captures errno after the call.
        // Hint: Linker.Option.captureCallState("errno")

        // TODO 2: Create the downcall handle for "transmit_distress_beacon", passing the capture option as a third argument.
        // C signature: int transmit_distress_beacon(int frequency_band)
        // Important: the capture option is the third argument when making a downcall handle.
        MethodHandle sendBeacon = null;

        // TODO 3: Get the built-in layout that FFM uses to store the captured state.
        // Hint: Linker.Option.captureStateLayout()
        StructLayout stateLayout = null;

        // GIVEN VarHandle to read errno out of the captured state
        VarHandle errnoHandle = stateLayout.varHandle(MemoryLayout.PathElement.groupElement("errno"));

        try (Arena arena = Arena.ofConfined()) {

            // GIVEN allocate memory to hold the captured errno state
            MemorySegment capturedState = arena.allocate(stateLayout);

            int frequencyBand = 2; // try 1, 2, and 3 to see different errors

            System.out.println("[Java] Attempting to broadcast distress signal...");

            // TODO 4: Invoke the handle capturedState must be the first argument, then frequencyBand.
            int result = 0;

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
                }  else if (errCode == 966) {
                    System.out.println("Mainframe Error 966: SPECIAL ORDERS.");
                    System.out.println("Do not abandon the ship. Bring back all Xenomorphic Materials, Priority One.");
                } else {
                    System.out.println("Unknown Critical Failure: " + errCode);
                }

                System.out.println("\nMOTHER: No distress signal transmitted. No rescue is coming. You are on your own.");
            } else {
                System.out.println(">>> TRANSMISSION SUCCESSFUL. Hold your ground, Colonial Marines are en route. <<<");
            }
        }
    }
}


