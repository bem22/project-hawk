
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <cstdio>
#include <cstring>
#include <iostream>
#include <thread>
#include <zconf.h>
#include "Client.h"
#include "Server.h"
#include <map>

int main() {
    int port = 4999;
    std::map<int, Client> clients;
    auto *srv = new Server(AF_INET, SOCK_STREAM, 0, port);
    if(srv->bind()) {
        std::cout<<"Bound on port: "<<srv->get_int_port()<<" at address: "<<srv->get_str_address()<<'\n';

        // Pass the listen() function to the thread and srv as argument
        std::thread listener(&Server::listen, srv);
        while(true){
            sleep(2);
            std::cout<<std::flush;
            std::cout<<"Thread 1 is working\n";
        }
        listener.join();
    } else {
        std::cout<<"didn't work, sorry\n";
    }

    return 0;
}
