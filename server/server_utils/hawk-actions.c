#include "hawk-packets.h"
#include <stdio.h>

int process_packet(packet *p) {
    switch(p->packet_type) {
        case LAND:
            printf("%s\n", "Drone landing");
            break;
        case ARM:
            printf("%s\n", "Drone armed");
            break;
        case DARM:
            printf("%s\n", "Drone disarmed");
            break;
        case STM:

        case UNKNOWN:
            printf("%s\n", "unknown action");
    }

    //TODO: Depending on the packet size, extract the rest of the packet
    //TODO: Create a packet pointer and call hawk-packets util function to fill the members
    //TODO: Take actions depending on the packet type/members
}