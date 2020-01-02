#include "connection.h"
#include "hawk-packets.h"
#include "hawk-actions.h"
#include <pthread.h>

size_t header_buffer_size = 30;

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

int read_packet_params(packet *p) {
    int recv_length;

    char* params_buffer;
    size_t params_buffer_size = p->packet_len - header_buffer_size;

    // Declare, allocate and memset the params buffer and its size
    params_buffer = (char *) malloc(params_buffer_size + 1);
    if (!params_buffer) {
        return 1;
    }
    memset(params_buffer, '\0', sizeof(char)*(params_buffer_size + 1));

    recv_length = recv(remote_fd, params_buffer, params_buffer_size, 0);

    if(recv_length == 0) {
        free(params_buffer);
        return 1;
    }

    char param_count_string[3] = {0};
    strncpy(param_count_string, params_buffer+7,  2);

    int param_count = atoi(param_count_string);
    p->param_size = param_count;

    params_buffer+=10;

    for(int i=0; i<param_count; i++) {
        p->params[i] = calloc(5, sizeof(char));
        strncpy(p->params[i], params_buffer + 5 * i,4);
    }

    return 0;
}

void *handle_connection() {
    // Reconvert the object from void pointer

    char buf[1];
    char* buffer = NULL;

    // Check if the connection is still alive. while alive... do
    while (recv(remote_fd, &buf,1, MSG_PEEK | MSG_DONTWAIT) != 0) {
        packet *packet;
        packet = malloc(sizeof(struct packet));
        // Declare, allocate and memset the header buffer and its size
        buffer = (char *) malloc(header_buffer_size + 1);
        if (!buffer) {
            free(packet);
            return NULL;
        }
        memset(buffer, '\0', sizeof(char) * (header_buffer_size + 1));

        // Variable to hold the number of bytes - return value of recv
        size_t recv_length = 0;

        recv_length = recv(remote_fd, buffer, header_buffer_size, 0);

        if (recv_length == 0) {
            free(packet);
            free(buffer);
            break; // client died
        }

        if(strncmp(buffer, "HAWK 1.0", 4) == 0) {
            init_packet_params(buffer, packet);
            process_packet(packet, read_packet_params);
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

            free(worker);
        }
    }
}

