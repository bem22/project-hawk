//
// Created by bem22 on 14/10/2019.
//

#ifndef SERVER_CLIENT_H
#define SERVER_CLIENT_H


#include <netinet/in.h>
#include <vector>

class Client {
private:
    int peer_socket;

    char peer_addr_str[16];

public:
    sockaddr *peer_addr;
    Client();
    int get_sock();
    int set_socket(int socket);
    sockaddr* get_sock_addr();
    char* get_str_addr();
};


#endif //SERVER_CLIENT_H
