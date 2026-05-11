package com.davidvlijmincx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class Assignment09_Upcall extends MainframeTerminal {

    public static void main(String[] args) throws Throwable {
        Linker linker = Linker.nativeLinker();
        String libPath = getLibPath();
        SymbolLookup shipSystems = SymbolLookup.libraryLookup( libPath, Arena.ofAuto());

        try (Arena arena = Arena.ofConfined()) {

            // 2. Create a MethodHandle pointing to our Java method
            MethodHandle javaAlarmHandle = MethodHandles.lookup().findStatic(
                    Assignment09_Upcall.class,
                    "onMotionDetected",
                    java.lang.invoke.MethodType.methodType(void.class, int.class, float.class)
            );

            // 3. Define the C-side signature of the upcall
            FunctionDescriptor alarmDescriptor = FunctionDescriptor.ofVoid(
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_FLOAT
            );

            // 4. THE MAGIC: Convert the Java MethodHandle into a C Function Pointer (MemorySegment)
            MemorySegment upcallStub = linker.upcallStub(javaAlarmHandle, alarmDescriptor, arena);

            // 5. Find the C functions
            MethodHandle registerTracker = linker.downcallHandle(
                    shipSystems.find("register_motion_tracker").orElseThrow(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS) // Accepts our function pointer
            );

            MethodHandle simulateSweep = linker.downcallHandle(
                    shipSystems.find("simulate_sensor_sweep").orElseThrow(),
                    FunctionDescriptor.ofVoid()
            );

            // 6. Execute!
            // Pass the Java function to C
            registerTracker.invokeExact(upcallStub);

            // Tell C to run the simulation, which will fire the upcalls back to Java
            simulateSweep.invokeExact();
        }
    }


    // Define the Java method that C will call.
    public static void onMotionDetected(int sectorId, float velocity) {
        var text = "[MOTION ALARM] Movement detected in Sector " + sectorId + "! ";
        if (velocity > 10.0f) {
            System.out.println(text + "TARGET IS SPRINTING at " + velocity + " m/s! <<<");
        } else {
            System.out.println(text + "Target velocity: " + velocity + " m/s. It's stalking...");
        }
    }
}


