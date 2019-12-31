#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "socket.h"
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>

#define PORT 5000
#define IP "127.0.0.1"
int client_socket;

int main(int argc, char* argv[]) {
    int port;
    char address[16];

    // Arguments handling
    if(argc != 2) {
        fprintf(stdout, "No params provided, assuming:localhost, port:5000\n");
        strcpy(address, "192.168.0.14");
        port = 5000;
    } else {
        if(strlen(argv[0]) <= 16) {
            strcpy(address, argv[0]);
            port = (int) strtol(argv[1], NULL, 10);
        }
        else {
            fprintf(stderr, "Bad args\n");
            return 1;
        }
    }
    struct sockaddr_in  server_addr;
    server_addr.sin_addr.s_addr = inet_addr(address);
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    client_socket = create_sock();
    
    if(connect(client_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) >= 0) {
        printf("%s","Connected to the server\n");
    }
    else {
        fprintf(stderr,"%s", "Error connecting to the server\n");
        return 1;
    }
    
    char* p;
    while(client_socket) {
        fgetc(stdin);
        p = malloc(39 * sizeof(char));
        strcpy(p, "HAWK 1.0\nCHNK: LAND \nSIZE: 49   \nPARAM: 99\n\n");

        // I observed that some delay must be induced
        // in order to be able to send very long 
        // messages. I tested with 86kB and it needs ~5000us
        // usleep(5000);
        
        if(!p) {
            fprintf(stderr, "%s", "Memory could not be allocated");
            return -1;
        }

        if(feof(stdin)) {
            free(p);
            printf("Triggered feof\n");
            return 0;
        }

        if(client_socket && p) {
            int no_bytes = write(client_socket, p, strlen(p));
            printf("bytes sent: %drone_utils\n", no_bytes);
            fflush(stdout);
            if(no_bytes == -1) {
                fprintf(stderr, "Server has gone down.");
                return 1;
            }
        }

        free(p);
    }
    fprintf(stderr, "%s", "The server is offline.");
    fflush(stderr);
    return 1;
}
