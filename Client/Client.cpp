#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include<cstdio>
#include<sys/un.h>
#include<cstdlib>
#include "Client.h"

Client::Client() = default;

Client::Client(char *ip, short family, unsigned short port, int stream) {
    servaddr.sin_addr.s_addr = inet_addr(ip);
    servaddr.sin_family = family;
    servaddr.sin_port = htons(port);
    csocket = socket(family, stream, 0);

    bzero(&this->servaddr, sizeof(this->servaddr));
}

int Client::getsocket() {
    if(this->csocket) {
        return csocket;
    }

    return -1;
}

void Client::setip(char *ip) {
    this->servaddr.sin_addr.s_addr = inet_addr(ip);
}

in_addr_t Client::getip() {
    return this->servaddr.sin_addr.s_addr;
}

int Client::start() {
    if (connect(this->csocket, (struct sockaddr*)&servaddr, sizeof(servaddr)) != 0) {
        printf("connection with the server failed...\n");
        exit(0);
    }
    else { printf("connected to the server..\n"); }
    return 0;
}
