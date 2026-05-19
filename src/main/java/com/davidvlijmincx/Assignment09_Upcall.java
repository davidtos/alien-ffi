package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class Assignment09_Upcall extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        // GIVEN
        Linker linker = Linker.nativeLinker();
        SymbolLookup shipSystems = SymbolLookup.libraryLookup(getLibPath(), Arena.ofAuto());

        try (Arena arena = Arena.ofConfined()) {

            // TODO 1: Get a MethodHandle pointing to the Java method that C will call back into.
            // Hint: MethodHandles.lookup().findStatic(class, methodName, MethodType.methodType(returnType, paramTypes...))
            MethodHandle javaAlarmHandle = null;

            // TODO 2: Describe the upcall's signature as a FunctionDescriptor.
            // The Java method C will call is: void onMotionDetected(int sectorId, float velocity)
            FunctionDescriptor alarmDescriptor = null;

            // TODO 3: Convert the Java MethodHandle into a C function pointer.
            // This is the core of an upcall, linker.upcallStub() wraps Java code in a native callable.
            // The arena controls the lifetime of the stub.
            // Hint: linker.upcallStub(methodHandle, descriptor, arena)
            MemorySegment upcallStub = null;

            // TODO 4: Create a downcall handle for "register_motion_tracker" and _invoke_ it,
            // passing the upcall stub as the function pointer argument.
            // C signature: void register_motion_tracker(void (*callback)(int, float))
            // Pass upcallStub as the function pointer argument
            // C expects a void(*)(int, float).

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


