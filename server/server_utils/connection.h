#ifndef SERVER_CONNECTION_H
#define SERVER_CONNECTION_H

#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define PORT 5000
#define LOCALHOST "127.0.0.1"
#define MAX_PEER 1

typedef struct client {
    int socket;
    struct sockaddr address;
    pthread_t *thread;
} client;


struct sockaddr_in server_addr;
int server_sock;
int remote_fd;
struct sockaddr remote_addr;
socklen_t in_size;
client remote;

short connection_count;

void host_setup();

int bind_port();

void start_server();

void *handle_connection();

#endif //SERVER_CONNECTION_H
