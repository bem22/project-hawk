#ifndef SERVER_HAWK_PACKETS_H
#define SERVER_HAWK_PACKETS_H

#define UNKNOWN -1
#define ARM 1
#define DARM 2
#define TELE 3
#define MODE 4
#define STM 5
#define SHM 6
#define AUTH 7
#define LAND 8

typedef struct { char *key; int val;} t_symstruct;

static t_symstruct lookuptable[] = {
        {"ARM", ARM},       // Arm action
        {"DARM", DARM},     // Disarm action
        {"TELE", TELE},     // Telemetry request action
        {"MODE", MODE},     // Flight mode request action
        {"STM", STM},       // Stick movement data
        {"SHM", SHM},       // Shoulder movement data
        {"AUTH", AUTH},     // Auth request action
        {"LAND", LAND}      // Land action
};

typedef struct node {
    void* value;
    struct node* next;
} node;

typedef struct list {
    node *node;
} list;


typedef union params {
    list params;
    char* token;
    int coordinates[2];
} params;

typedef struct packet {
    int packet_len;
    int packet_type;
    char* params;
    params para;
} packet;


int process_packet(packet *p, int (*f)(packet *p));
void init_packet_params(char* tcp_payload, packet* p);
int key_from_string(char* key);


#endif //SERVER_HAWK_PACKETS_H
