//
// Created by bem22 on 12/10/2019.
//

#ifndef CLIENT_CLIENT_H
#define CLIENT_CLIENT_H


#include <netinet/in.h>

class Client {
private:
    int client_socket;
    struct sockaddr_in server_addr;

public:
    Client ();
    in_addr_t get_ip();
    void set_ip(char *ip);
    int get_socket();
    int start();
    int bind();
};


#endif //CLIENT_CLIENT_H
