#include "hawk-packets.h"
#include <stdio.h>
#include "../drone_utils/state.h"

bool action_arm() {
    if(drone_state.ARMED) {
        return 0;
    }
    else {
        state_init();
        return 1;
    }
}

bool action_disarm() {
    if(state_check_parked()) {
        drone_state.ARMED = 0;
        return 1;
    }
    return 0;

}

bool action_land() {
    return 1;
}

bool action_update_axes() {
    return 1;
}

bool telemetry() {
    return 1;
}

int process_packet(packet *p, int (*update_packet)(void)) {
    switch(p->packet_type) {
        case LAND:
            printf("%s\n", "Drone landing");
            action_land();
            break;
        case ARM:
            printf("%s\n", "Drone armed");
            action_arm();
            break;
        case DARM:
            printf("%s\n", "Drone disarmed");
            action_disarm();
            break;
        case STM:
            update_packet();
            action_update_axes();
            break;
        case TELE:
            printf("%s\n", "Telemetry");
            telemetry();
            break;
        case UNKNOWN:
            printf("%s\n", "unknown action");
    }
    return 1;
}