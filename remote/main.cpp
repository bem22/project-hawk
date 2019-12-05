// Client side C/C++ program to demonstrate Socket programming 
#include <stdio.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include "Client.h"

#define PORT 4998

int main(int argc, char const *argv[])
{
    auto cl1 = new Client();
    cl1->start();
    return 0;
}
