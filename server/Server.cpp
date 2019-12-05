//
// Created by bem22 on 14/10/2019.
//

#include <thread>
#include <zconf.h>
#include <iostream>
#include <arpa/inet.h>
#include <backward/strstream>
#include <sstream>
#include <vector>
#include "Server.h"
#include "Client.h"

// Constructor
Server::Server(int prot, int sock_type, int type, int port) {
    // Set up server socket
    server_socket = socket(prot, sock_type, type);

    // Set up server_address (sockaddr_in)
    server_address.sin_family = prot;
    server_address.sin_addr.s_addr = inet_addr("127.0.0.1");
    server_address.sin_port = htons(port);
    addrlen = sizeof(struct sockaddr_in);

    // Copy the string address of the server to char* server_addr_str
    inet_ntop(AF_INET, &(server_address.sin_addr.s_addr), server_addr_str, INET_ADDRSTRLEN);

    clients = {};

    // Max no. of connections in the queue for accept()
    nocon = 12;
}

int Server::bind() {
    if(::bind(server_socket, (struct sockaddr*)&server_address, sizeof(sockaddr))){
        return 0;
    } return 1;
}

void Server::listen() {
    std::cout<< std::flush;
    ::listen(this->server_socket, this->nocon);
    while(true) {
        auto *client = new Client();
        clients.push_back(client);

        // This is a fd that can be polled on
        // TODO: read about read/write to fd`s
        int client_socket = accept(
                server_socket,
                (struct sockaddr*)client->get_sock_addr(),
                (socklen_t*) client->get_addr_len());
        client->set_socket(client_socket);
        std::cout<<"Client "<<client_socket<<" connected on port: "<<ntohs(client->get_in_port())<<'\n'<<std::flush;
    }
}

int Server::get_int_port() {
    return ntohs(this->server_address.sin_port);
}

char* Server::get_str_address() {
    return server_addr_str;
}
