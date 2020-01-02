#include "hawk-packets.h"
#include <stdio.h>
#include <malloc.h>
#include <stdlib.h>
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

bool action_update_axes(packet *p) {
    if(state_is_armed()) {
        drone_state.THROTTLE = atoi(p->params[4]);
        drone_state.PITCH = atoi(p->params[0]);
        drone_state.ROLL = atoi(p->params[1]);
        drone_state.YAW = atoi(p->params[2]);

        for(int i = 0; i< p->param_size; i++) {
            free(p->params[i]);
        }

        return 1;
    } else return 0;
}

bool telemetry() {
    return 1;
}

int process_packet(packet *p, int (*update_packet)(packet *p)) {
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
            update_packet(p);
            action_update_axes(p);
            printf("%d %d %d %d \n",drone_state.THROTTLE, drone_state.ROLL, drone_state.PITCH, drone_state.YAW);
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