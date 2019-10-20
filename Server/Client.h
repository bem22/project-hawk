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
    char peer_addr_ip[16];
    socklen_t peer_addr_len;
public:
    sockaddr peer_addr;
    sockaddr* get_sock_addr();
    int client_type;
    Client();
    int get_sock();
    int set_socket(int socket);
    char* get_str_ip();

    socklen_t *get_addr_len();

    in_port_t get_in_port();
};


#endif //SERVER_CLIENT_H
