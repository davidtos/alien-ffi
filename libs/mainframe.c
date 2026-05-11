#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif


void print_with_prefix(const char *str) {
    setbuf(stdout, NULL);
    printf("%s%s\n", "MU/TH/TR: ", str);
}

// Assignment 1:
int connect_to_mainframe(int access_code) {
    if (access_code == 42) {
        print_with_prefix("Handshake accepted. Connection established.\n");
        return 200;
    }
    print_with_prefix("ACCESS DENIED.\n");
    return 403;
}

// Assignment 2:
void get_current_stardate(double *segment_ptr) {
    if (segment_ptr == NULL) return;

    time_t now = time(NULL);
    struct tm *t = localtime(&now);

    // tm_year is years since 1900
    int year = t->tm_year + 1900;
    // tm_yday is days since January 1st (0-365)
    int day_of_year = t->tm_yday;

    // Logic for leap years
    double days_in_year = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 366.0 : 365.0;

    // Formula: (Year - 2000) * 1000 + (fraction of year * 1000)
    double stardate = (year - 2000) * 1000 + (day_of_year / days_in_year) * 1000;

    *segment_ptr = stardate;
}

// Assignment 3:
const char* get_threat_assessment(int threat_level) {
    if (threat_level >= 9) return "PRIORITY OVERRIDE: CREW EXPENDABLE. RETURN SPECIMEN.";
    if (threat_level >= 7) return "CRITICAL: Direct encounter imminent. Evacuate sector.";
    if (threat_level >= 4) return "WARNING: Biomass detected. Maintain safe distance.";
    return "CLEAR: No xenomorphic signatures detected.";
}

// Assignment 4:
void authenticate_biometrics(const char* bio_sig) {
    char buffer[256];
    print_with_prefix("Reading data stream... ");
    snprintf(buffer, sizeof(buffer), "Authenticated: %s", bio_sig);
    print_with_prefix(buffer);
    print_with_prefix("FULL SHIP ACCESS GRANTED");

}

const char* MSG_CLEAR = "Sector Clear";
const char* MSG_BIOMASS = "WARNING! Non-terrestrial biomass detected!";
const char* MSG_ERROR = "ERROR: Scan failed. No sectors detected in array.";

// Assignment 5:
void run_sector_scan(int* sector_ids, const char** out_reports, int count) {
    // 1. Error handling for 0 length
    if (count <= 0) {
        printf("%s\n", MSG_ERROR);
        return;
    }

    // Initialize random seed (usually done once in main, but kept here for context)
    srand(time(NULL));

    // 2. Pick one random index to definitely be the biomass
    int biomass_index = rand() % count;

    for (int i = 0; i < count; i++) {
        if (i == biomass_index) {
            // Guaranteed biomass sector
            out_reports[i] = MSG_BIOMASS;
        } else {
            // All other sectors are clean
            out_reports[i] = MSG_CLEAR;
        }
    }
}

// Assignment 6:
// The struct they need to map
typedef struct {
    int entity_id;
    float threat_level;
    long mass_kg;
    int produces_acid;
} BiomassSignature;

// The downcall target
void execute_deep_scan(int sector_id, BiomassSignature* output_scan) {
    setbuf(stdout, NULL);
    printf("MU/TH/TR: Initializing deep scan on Sector %d...\n", sector_id);

    // Simulate finding the terrifying alien
    output_scan->entity_id = 121;
    output_scan->threat_level = 99.9f;
    output_scan->mass_kg = 181;
    output_scan->produces_acid = 1;

    printf("MU/TH/TR: Scan complete. Telemetry written to memory buffer.\n");
}

// Assignment 7:
typedef struct {
    char   classification;  // offset 0,  1 byte
                            // offset 1-3: 3 bytes padding (inserted by C compiler)
    int    sector_id;       // offset 4,  4 bytes
    double signal_strength; // offset 8,  8 bytes
    char   active;          // offset 16, 1 byte
                            // offset 17-23: 7 bytes padding (inserted by C compiler)
} SensorReading;            // sizeof = 24 bytes

void scan_sector(int sector_id, SensorReading* output) {
    output->classification = 'X';
    output->sector_id = sector_id;
    output->signal_strength = 98.6;
    output->active = 1;
}

// Assignment 8:

// Define the shape of the callback function (The Upcall)
// It will tell us WHICH sector, and HOW FAST the entity is moving.
typedef void (*MotionCallback)(int sector_id, float velocity);

MotionCallback java_alarm_trigger = NULL;

void register_motion_tracker(MotionCallback callback) {
    java_alarm_trigger = callback;
    print_with_prefix("Motion tracker linked to Java containment system.\n");
}

void simulate_sensor_sweep() {
    print_with_prefix("Initiating sensor sweep...\n");
    if (java_alarm_trigger != NULL) {
        // The C code calls the Java code!
        java_alarm_trigger(4, 1.2f); // Sector 4, moving slowly...
        java_alarm_trigger(4, 5.8f); // Speeding up...
        java_alarm_trigger(5, 12.4f); // It just moved to Sector 5! It's running!
    } else {
        print_with_prefix("ERROR: No alarm trigger registered!\n");
    }
}

// Assignment 9:
int transmit_distress_beacon(int frequency_band) {
    setbuf(stdout, NULL);
    printf("MU/TH/TR: Powering up comms array on frequency %d...\n", frequency_band);

    if (frequency_band == 1) {
        errno = 939;
    }
    if (frequency_band == 2) {
        errno = 966;
    }
    else {
        errno = 937; // No distress signal allowed
    }

    return -1;
}

// Assignment 10:

#define DUMP_SIZE 10240       // 10KB memory dump
#define DIRECTIVE_OFFSET 4096 // The secret is hidden here
#define DIRECTIVE_LENGTH 64   // Exact length of the slice needed

char* core_dump = NULL;

// Simulates extracting a large block of raw mainframe memory
void* get_core_dump() {
    if (core_dump == NULL) {
        core_dump = (char*)malloc(DUMP_SIZE);
        memset(core_dump, 0xAA, DUMP_SIZE); // Fill with dummy hex data

        // Inject the hidden directive at the specific offset
        const char* secret = "SPECIAL ORDER 937: RETURN ALIEN LIFEFORM, AT ALL COST";
        memcpy(core_dump + DIRECTIVE_OFFSET, secret, strlen(secret) + 1);
    }
    return core_dump;
}

// Function to decrypt the directive.
// It requires the slice to be exactly the right length to work.
void decrypt_dump(const char* memory_slice, int length) {
    if (length != DIRECTIVE_LENGTH) {
        printf("MU-TH-UR: ERROR. Invalid sector size. Decryption failed.\n");
        return;
    }

    printf("MU-TH-UR: Decrypting sector...\n");
    printf("MU-TH-UR: DIRECTIVE OVERRIDE: [%s]\n", memory_slice);
}


// Assignment 12:
int analyze_sensor_sweep(int32_t* coordinates, int ping_count) {
    int max_threat = 0;

    for (int i = 0; i < ping_count * 2; i += 2) {
        int x = coordinates[i];
        int y = coordinates[i+1];
        int threat = (x * x) + (y * y);
        if (threat > max_threat) {
            max_threat = threat;
        }
    }
    return max_threat;
}

// Assignment 13:

typedef struct {
    int32_t id;
    int32_t threat_level;
    float direction;
    float movement_speed;
} AlienSignature;


AlienSignature** analyze_sensor_big_sweep(int ping_count) {

    AlienSignature** signatures = malloc(ping_count * sizeof(AlienSignature*));
    if (signatures == NULL) return NULL;

    for (int i = 0; i < ping_count; i++) {
        signatures[i] = malloc(sizeof(AlienSignature));
        if (signatures[i] != NULL) {
            int x = ping_count +i / 2;
            int y = ping_count +i / 2 + 1;

            signatures[i]->id = i + 1000;
            signatures[i]->threat_level = (x * x) + (y * y);
            signatures[i]->direction = (float)(x % 360);
            signatures[i]->movement_speed = (float)(y * 0.5f);
        }
    }

    return signatures;
}

// Memory cleanup function
void free_sensor_sweep(AlienSignature** signatures, int ping_count) {
    if (signatures == NULL) return;

    for (int i = 0; i < ping_count; i++) {
        if (signatures[i] != NULL) {
            free(signatures[i]);
        }
    }
    free(signatures);
}
