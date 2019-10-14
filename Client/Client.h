//
// Created by bem22 on 12/10/2019.
//

#ifndef CLIENT_CLIENT_H
#define CLIENT_CLIENT_H


#include <netinet/in.h>

class Client {
private:
    int csocket;
    struct sockaddr_in servaddr{};
public:
    Client ();
    explicit Client (char* ip, short family, unsigned short port, int type);

    in_addr_t getip();
    void setip(char *ip);
    int getsocket();
    int start();
};


#endif //CLIENT_CLIENT_H
