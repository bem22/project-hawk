#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include<cstdio>
#include<sys/un.h>
#include<cstdlib>
#include "Client.h"

Client::Client() {
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(4998);
    client_socket = socket(AF_INET, SOCK_STREAM, 0);
}

int Client::get_socket(){
    return client_socket;
}

void Client::set_ip(char *ip) {
    this->server_addr.sin_addr.s_addr = inet_addr(ip);
}

in_addr_t Client::get_ip() {
    return this->server_addr.sin_addr.s_addr;
}

int Client::bind() {
    return bind(client_socket, );
}

int Client::start() {
    int i=1;
    if (i = connect(this->client_socket, (struct sockaddr*)&this->server_addr, sizeof(this->server_addr)) != 0) {
        printf("connection with the server failed...\n");
    }
    else { printf("connected to the server..\n"); }
    return 0;
}
