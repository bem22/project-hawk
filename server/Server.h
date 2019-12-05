//
// Created by bem22 on 14/10/2019.
//

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-private-field"
#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H


#include <netinet/in.h>
#include "Client.h"

class Server {
private:
    int server_socket;
    struct sockaddr_in server_address;
    int addrlen;
    int nocon;
    std::vector<Client*> clients;
    char server_addr_str[16];
public:
    // Constructor
    Server(int inet, int sock_type, int protocol, int port);

    int bind();
    void listen();
    int get_int_port();
    char *get_str_address();
};

#endif //SERVER_SERVER_H

#pragma clang diagnostic pop