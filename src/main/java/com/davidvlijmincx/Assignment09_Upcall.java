package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Assignment09_Upcall extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        // GIVEN
        Linker linker = Linker.nativeLinker();
        SymbolLookup shipSystems = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        try (Arena arena = Arena.ofConfined()) {

            // TODO 1: Get a MethodHandle pointing to the Java method that C will call back into.
            MethodHandle javaAlarmHandle = MethodHandles.lookup().findStatic(
                    Assignment09_Upcall.class,
                    "onMotionDetected",
                    java.lang.invoke.MethodType.methodType(void.class, int.class, float.class)
            );

            // TODO 2: Describe the upcall's signature as a FunctionDescriptor.
            FunctionDescriptor alarmDescriptor = FunctionDescriptor.ofVoid(
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_FLOAT
            );

            // TODO 3: Convert the Java MethodHandle into a C function pointer.
            MemorySegment upcallStub = linker.upcallStub(javaAlarmHandle, alarmDescriptor, arena);

            // TODO 4: Create a downcall handle for "register_motion_tracker" and _invoke_ it,
            MethodHandle registerTracker = linker.downcallHandle(
                    shipSystems.find("register_motion_tracker").orElseThrow(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS) // Accepts our function pointer
            );

            registerTracker.invokeExact(upcallStub);

            System.out.println("MOTHER: Motion tracker online. Activating sensor sweep...");
            System.out.println("MOTHER: Do not move.\n");

            // Given
            MethodHandle simulateSweep = linker.downcallHandle(
                    shipSystems.find("simulate_sensor_sweep").orElseThrow(),
                    FunctionDescriptor.ofVoid()
            );

            // Tell C to run the simulation, which will fire the upcalls back to Java
            simulateSweep.invokeExact();
        }

        System.out.println("MOTHER: Sensor sweep concluded. A core dump has been triggered.");
    }


    // The Java method that C will call.
    public static void onMotionDetected(int sectorId, float velocity) {
        var text = "[MOTION ALARM] Movement detected in Sector " + sectorId + "! ";
        if (velocity > 10.0f) {
            System.out.println(text + "TARGET IS SPRINTING at " + velocity + " m/s! <<<");
        } else {
            System.out.println(text + "Target velocity: " + velocity + " m/s. It's stalking...");
        }
    }
}


