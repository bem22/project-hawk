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
    return peer_addr;
}

char* Client::get_str_addr() {
    return peer_addr->sa_data;
}

int Client::set_socket(int socket) {
    if(this->peer_socket) {
        return 0;
    } else {
        this->peer_socket = socket;
        return 1;
    }
}
