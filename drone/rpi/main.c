#include "drone_utils/ppmer.h"
#include "network_utils/connection.h"

int main(int argc, char *argv[]) {
    init(4, 8, 20);

    start_server();

    destroy();
}
