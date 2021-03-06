#include "hawk-packets.h"
#include <stdio.h>
#include <stdlib.h>
#include "../drone_utils/state.h"
#include "../drone_utils/ppmer.h"
#include "../peripheral_utils/i2c_reader.h"
#include "connection.h"
#include "packetizer.h"

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
    drone_state.ARMED = 0;
    return 1;
}

bool action_land() {
    send_tcp_packet(buildPacket("BAT ", 1, 5, get_battery_voltage()), 1024);
    return 1;
}

bool action_update_axes(packet *p) {
    if(state_is_armed()) {
        drone_state.THROTTLE = atoi(p->params[0]); //
        drone_state.ROLL = atoi(p->params[1]);
        drone_state.PITCH = atoi(p->params[2]);
        drone_state.YAW = atoi(p->params[3]); //
        drone_state.AUX1 = atoi(p->params[4]);
        drone_state.AUX2 = atoi(p->params[5]);
        drone_state.AUX3 = atoi(p->params[6]);
        drone_state.AUX4 = atoi(p->params[7]);
        return 1;
    } else return 0;
}

bool telemetry() {
    return 1;
}

int process_tcp_packet(packet *p, int (*update_packet)(packet *p)) {
    switch(p->packet_type) {
        case LAND:
            printf("%s\n", "Drone landing");
            action_land();
            break;
        case ARM:
            fflush(stdout);
            printf("%s\n", "Drone armed");
            action_arm();
            break;
        case DARM:
            printf("%s\n", "Drone disarmed");
            action_disarm();
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

int process_udp_packet(packet *p) {
    switch(p->packet_type) {
        case STM:
            action_update_axes(p);
            break;
    }

    return 1;
}