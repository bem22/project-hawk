#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "hawk-packets.h"
#include "string-utils.h"

#define UNKNOWN -1
#define ARM 1
#define DARM 2
#define TELE 3
#define MODE 4
#define STM 5
#define SHM 6
#define AUTH 7
#define LAND 8

#define NKEYS (sizeof(lookuptable) / sizeof(t_symstruct))

void init_packet_params(char* tcp_payload, packet* p) {
    char packet_length_str[6] = {'\0'};
    memcpy(packet_length_str, tcp_payload + 26, 5);
    p->packet_len = atoi(packet_length_str);

    char packet_type_str[5] = {'\0'};
    memcpy(packet_type_str, tcp_payload + 15 , 4);
    replace(packet_type_str, ' ', '\0');

    p->packet_type = key_from_string(packet_type_str);
}

int key_from_string(char* key) {
    int i;


    for (i=0; i < NKEYS; i++) {
        t_symstruct sym = lookuptable[i];
        if (strncmp(sym.key, key, strlen(key)) == 0)
            return sym.val;
    }
    return UNKNOWN;
}