#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <pthread.h>
#include "server_utils/hawk-actions.h"
#include "server_utils/hawk-packets.h"

typedef struct client {
    int socket;
    struct sockaddr address;
    pthread_t *thread;
} Client;

FILE *fp;
int *line_no = NULL;
int thread_counter = 0;
int max_no_threads = 15;
pthread_mutex_t file_mut;
void *filewrit(void *param) {
    // Reconvert the object from void pointer
    Client *client = param;
    char buf[1];
    char* buffer = NULL;

    // Check if the connection is still alive. while alive... do
    while (recv(client->socket, &buf,1, MSG_PEEK | MSG_DONTWAIT) != 0) {
        packet *packet;

        packet = malloc(sizeof(packet));

        // Declare, allocate and memset the buffer and its size

        size_t buffer_size = 43;

        buffer = (char *) malloc(buffer_size + 1);
        if (!buffer) {
            return NULL;
        }
        memset(buffer, 0, buffer_size + 1);

        // Variable to hold the number of bytes - return value of recv
        size_t recv_length = 0;

        recv_length = recv(client->socket, buffer, buffer_size, 0);

        if (recv_length == 0) {
            free(buffer);
            break; // Client died
        }

        if(strncmp(buffer, "HAWK 1.0", 4) == 0) {
            init_packet_params(buffer, packet);
            
            process_packet(packet);

        }

        free(buffer);
        free(packet);
    }

    // Free the thread pointer
    free(client->thread);
    printf("Client %d went away :(\n", client->socket);
    free(client);
    return NULL;
}

int main(int argc, char *argv[]) {
    int port;
    char address[16];

    // Arguments handling
    if(argc != 2) {
        fprintf(stdout, "No params provided, assuming:localhost, port:5000\n");
        strcpy(address, "127.0.0.1");
        port = 5001;
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

    struct sockaddr_in server_addr;
    server_addr.sin_port = htons(port);
    server_addr.sin_addr.s_addr = inet_addr(address);
    server_addr.sin_family = AF_INET;
    bzero(&(server_addr.sin_zero),8);

    int server_sock = socket(AF_INET, SOCK_STREAM, 0);

    fflush(stdin);

    if(bind(server_sock, (struct sockaddr*)&server_addr, sizeof(server_addr)) == 0) {
        printf("%s", "Server bound on port 5000\n");
    } else {
        fprintf(stderr, "Unable to bind to the specified port\n");
        return 1;
    }

    if(listen(server_sock, 5) == -1) {
        printf("listen error");
    }

    int cfd;
    struct sockaddr caddr;
    socklen_t in_size = sizeof(caddr);


    printf("Ready to accept connections\n");
    while(1) {
        // Create client fds, the main thread will be busy here waiting for clients
        if ((cfd = accept(server_sock, &caddr, &in_size)) > -1) {
            printf("%s", "client connected\n");
            //if(thread_counter < max_no_threads) {
                // Create a new client object from cfd+struct
                Client *client = (Client*) malloc(sizeof(Client));
                //Save the stack values in the struct (cfd and caddr get rewritten)
                client->socket = cfd;
                client->address = caddr;

                // Create a thread as a pointer and save it to the list of threads
                pthread_t *worker = (pthread_t*) malloc(sizeof(pthread_t));
                thread_counter++;
                client->thread = worker;

                // Start the thread
                pthread_create(worker, NULL, filewrit, client);
                pthread_join(*worker, NULL);
            //} else {
            //    close(cfd);
            //    printf("Max thread no reached");
            //}
        }
    }

    // Not entirely necessary now, because main thread is in a while(1)
    // Same as joining the worker threads
    free(line_no);
    line_no = NULL;
    fclose(fp);
    return 0;
}
