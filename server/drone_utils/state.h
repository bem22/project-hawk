#ifndef SERVER_STATE_H
#define SERVER_STATE_H

#include <stdbool.h>

#define MIN_CTRL 1000
#define MAX_CTRL 2000

typedef struct state {
    bool ARMED;
    unsigned short THROTTLE;
    unsigned short YAW;
    unsigned short PITCH;
    unsigned short ROLL;
} state;

state drone_state;

bool state_init();
bool state_check_parked();

#endif //SERVER_STATE_H
