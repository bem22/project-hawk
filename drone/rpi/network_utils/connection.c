#include "connection.h"
#include "hawk-packets.h"
#include "hawk-actions.h"
#include "../drone_utils/ppmer.h"
#include "../drone_utils/state.h"
#include "zconf.h"
#include <pthread.h>
#include <fcntl.h>
#include <errno.h>


size_t header_buffer_size = 31;
int optval;
connected = 1;

void host_setup() {
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_family = AF_INET;
    bzero(&(server_addr.sin_zero), 8);


    server_sock_tcp = socket(AF_INET, SOCK_STREAM, 0);
    server_sock_udp = socket(AF_INET, SOCK_DGRAM, 0);

    optval = 1;
    setsockopt(server_sock_tcp, SOL_SOCKET, SO_REUSEADDR, (const void *)&optval, sizeof(int));
    setsockopt(server_sock_udp, SOL_SOCKET, SO_REUSEADDR, (const void *)&optval, sizeof(int));

    in_size = sizeof(remote_addr);
    connection_count = 0;
}

int bind_port() {
    if(bind(server_sock_tcp, (struct sockaddr*)&server_addr, sizeof(server_addr)) == 0 &&
            bind(server_sock_udp, (struct sockaddr*)&server_addr, sizeof(server_addr)) == 0) {
        printf("%s port %d.\n", "Server bound on", PORT );
        return 0;
    } else {
        fprintf(stderr, "Unable to bind on %d. Port busy.\n", PORT);
        exit(1);
    }
}

int read_tcp_packet_params(packet *p) {
    int recv_length;

    char* params_buffer;
    size_t params_buffer_size = p->packet_len - header_buffer_size;

    // Declare, allocate and memset the params buffer and its size
    params_buffer = (char *) malloc(params_buffer_size + 1);
    if (!params_buffer) {
        return 1;
    }
    memset(params_buffer, '\0', sizeof(char)*(params_buffer_size + 1));

    recv_length = recv(remote_fd_tcp, params_buffer, params_buffer_size, 0);

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
        strncpy(p->params[i], params_buffer + 5 * i,4);
    }

    return 0;
}

void *handle_tcp_connection() {
    // Reconvert the object from void pointer
    char buf[1];
    char* buffer = NULL;

    // Check if the connection is still alive. while alive... do
    while (recv(remote_fd_tcp, &buf, 1, MSG_PEEK | MSG_DONTWAIT) != 0) {
        packet *packet;
        packet = malloc(sizeof(struct packet));
        // Declare, allocate and memset the header buffer and its size
        buffer = (char *) malloc(header_buffer_size + 1);
        if (!buffer) {
            free(packet);
            break;
        }
        memset(buffer, '\0', sizeof(char) * (header_buffer_size + 1));

        // Variable to hold the number of bytes - return value of recv
        size_t recv_length = 0;

        recv_length = recv(remote_fd_tcp, buffer, header_buffer_size, 0);

        if (recv_length == 0) {
            free(packet);
            free(buffer);
            break;
        }

        if(strncmp(buffer, "HAWK 1.0", 4) == 0) {
            init_packet_fields(buffer, packet);
            process_tcp_packet(packet, read_tcp_packet_params);
        }

        free(buffer);
        free(packet);
    }
    connected = 0;
    drone_state.ARMED = 0;
    connection_count--;
    fflush(stdout);
    printf("Client %d went away :(\n", remote_fd_tcp);
    return NULL;
}

int send_tcp_packet(char* buffer, int buffer_size) {
    if(connected) {
        int qq = write(remote_fd_tcp, buffer, buffer_size);
        printf("%s, %d, %d\n", "TCP Packet SENT", qq, errno);
    }
}


int read_udp_packet_params(packet *p, char* buffer) {
    // This will read the number of params tag "PARAMC"
    char param_count_string[3] = {0};
    strncpy(param_count_string, buffer + header_buffer_size + 6,  2);

    // This will read the actual number and apply it to the packet
    int param_count = atoi(param_count_string);
    p->param_size = param_count;

    // Skip the read chars
    buffer+=(header_buffer_size + 9);

    for(int i=0; i<param_count; i++) {
        strncpy(p->params[i], buffer + 5 * i, 4);
    }

    return 0;
}

void *handle_udp_connection() {
    // Reconvert the object from void pointer
    char buffer[1024];
    int n;

    struct timeval tv;
    tv.tv_sec = 4;
    tv.tv_usec = 0;
    if (setsockopt(server_sock_udp, SOL_SOCKET, SO_RCVTIMEO,&tv,sizeof(tv)) < 0) {
        perror("Error");
    }

    while(connected) {
        n = recvfrom(server_sock_udp, buffer, 1024, 0,
                     (struct sockaddr *) &remote_addr, &in_size);
        if (n == -1) {
            break;
        }

        buffer[n] = '\0';

        packet *packet;
        packet = malloc(sizeof(struct packet));

        if(strncmp(buffer, "HAWK 1.0", 4) == 0) {
            init_packet_fields(buffer, packet);
            read_udp_packet_params(packet, buffer);
            process_udp_packet(packet);
        }
        free(packet);
    }

    return 0;
}

void start_server() {
    host_setup();
    bind_port();

    if(listen(server_sock_tcp, MAX_PEER) == -1) {
        printf("listen error");
        exit(1);
    }

    fflush(stdout);
    printf("Waiting for connections\n");

    while(connection_count < MAX_PEER) {

        // Create client fds, the main thread_tcp will be busy here waiting for clients
        if ((remote_fd_tcp = accept(server_sock_tcp, &remote_addr, &in_size)) > -1) {
            connection_count++;
            connected = 1;
            printf("%s", "Client connected!\n");

            // Create a thread_tcp as a pointer and save it to the list of threads
            pthread_t *tcp_handler_thread = (pthread_t*) malloc(sizeof(pthread_t));
            pthread_t *udp_handler_thread = (pthread_t*) malloc(sizeof(pthread_t));

            remote.thread_tcp = tcp_handler_thread;
            remote.thread_udp = udp_handler_thread;

            // Start the thread_tcp and thread_udp
            pthread_create(tcp_handler_thread, NULL, &handle_tcp_connection, NULL);
            pthread_create(udp_handler_thread, NULL, &handle_udp_connection, NULL);
            pthread_create(ppm_handler_thread, NULL, &update_channels, NULL);

            pthread_join(*tcp_handler_thread, NULL);
            pthread_join(*udp_handler_thread, NULL);
            pthread_join(*ppm_handler_thread, NULL);

            free(tcp_handler_thread);
            free(udp_handler_thread);

        }
    }
}
