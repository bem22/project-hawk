#include "network_utils/connection.h"
#include "drone_utils/encoder.h"
int main(int argc, char *argv[]) {
    start_encoder();
    start_server();
    return 0;
}
