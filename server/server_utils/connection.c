#include "connection.h"
#include "hawk-packets.h"
#include "hawk-actions.h"
#include <pthread.h>

void host_setup() {
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_family = AF_INET;
    bzero(&(server_addr.sin_zero),8);
    server_sock = socket(AF_INET, SOCK_STREAM, 0);
    in_size = sizeof(remote_addr);
    connection_count = 0;
}

int bind_port() {
    if(bind(server_sock, (struct sockaddr*)&server_addr, sizeof(server_addr)) == 0) {
        printf("%s %d\n", "server bound on", PORT);
    } else {
        fprintf(stderr, "unable to bind on %d\n", PORT);
        exit(1);
    }
}

int read_packet_body(void) {
    return 1;
}

void *handle_connection() {
    // Reconvert the object from void pointer

    char buf[1];
    char* buffer = NULL;

    // Check if the connection is still alive. while alive... do
    while (recv(remote_fd, &buf,1, MSG_PEEK | MSG_DONTWAIT) != 0) {
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

        recv_length = recv(remote_fd, buffer, buffer_size, 0);

        if (recv_length == 0) {
            free(buffer);
            break; // client died
        }

        if(strncmp(buffer, "HAWK 1.0", 4) == 0) {
            printf("Packet is %s", buffer);
            init_packet_params(buffer, packet);
            process_packet(packet, read_packet_body);
        }

        free(buffer);
        free(packet);
    }

    printf("client %d went away :(\n", remote_fd);
    return NULL;
}

void start_server() {
    if(listen(server_sock, MAX_PEER) == -1) {
        printf("listen error");
        exit(1);
    }

    printf("waiting for connections...\n");

    while(connection_count < MAX_PEER) {

        // Create client fds, the main thread will be busy here waiting for clients
        if ((remote_fd = accept(server_sock, &remote_addr, &in_size)) > -1) {
            printf("%s", "client connected\n");

            // Create a thread as a pointer and save it to the list of threads
            pthread_t *worker = (pthread_t*) malloc(sizeof(pthread_t));

            remote.thread = worker;

            // Start the thread
            pthread_create(worker, NULL, handle_connection, NULL);
            pthread_join(*worker, NULL);
        }
    }
}

