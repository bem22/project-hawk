#include <stdio.h>
#include <sys/time.h>
#include <zconf.h>
#include <pigpio.h>
#include "drone_utils/ppmer.h"
int main(int argc, char *argv[]) {
    int updates = 0;
    init(4, 8, 20);


    for(int i=0; i<ppm_factory.channel_count; i++) {
        for(int power=1000; power<=2000; power+=10) {
            update_channel(i, power);
            updates++;
        }
    }

    printf("Total updates: %d", updates);

    destroy();
}
