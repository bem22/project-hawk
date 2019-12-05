//
// Created by bem22 on 14/10/2019.
//

#include <cstring>
#include "Client.h"

Client::Client() {
}

int Client::get_sock() {
    return peer_socket;
}

sockaddr *Client::get_sock_addr() {
    return &peer_addr;
}

socklen_t *Client::get_addr_len() {
    return &peer_addr_len;
}

char* Client::get_str_ip() {
    return peer_addr.sa_data;
}

in_port_t Client::get_in_port() {
    return((struct sockaddr_in*)&this->peer_addr)->sin_port;
}

int Client::set_socket(int socket) {
    if(this->peer_socket) {
        return 0;
    } else {
        this->peer_socket = socket;
        return 1;
    }
}
