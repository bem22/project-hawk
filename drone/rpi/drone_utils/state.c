#include "state.h"

bool state_init() {
    drone_state.ARMED = 1;
    drone_state.THROTTLE = 1000;
    drone_state.PITCH = 1500;
    drone_state.ROLL = 1500;
    drone_state.YAW = 1500;
    drone_state.AUX1 = 1000;
    drone_state.AUX2 = 1000;
    drone_state.AUX3 = 1000;
    drone_state.AUX4 = 1000;
}

bool state_check_parked() {
    if(!drone_state.ARMED &&
        drone_state.THROTTLE == 1000 &&
        drone_state.YAW == 1500 &&
        drone_state.PITCH == 1500 &&
        drone_state.ROLL == 1500) {
        return 1;
    }
    return 0;
}

bool state_is_armed() {
    return drone_state.ARMED;
}