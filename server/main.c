#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "server_utils/hawk-actions.h"
#include "server_utils/hawk-packets.h"
#include "server_utils/connection.h"

int main(int argc, char *argv[]) {
    host_setup();
    bind_port();
    start_server();

    return 0;
}
