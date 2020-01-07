#include <stdio.h>
#include <sys/time.h>
#include <zconf.h>
#include <pigpio.h>
#include "drone_utils/ppmer.h"
int main(int argc, char *argv[]) {

    int updates = 0;
    time_t start_time, end_time;

    init(4, 8, 20);

    start_time = time(NULL);

    for(int i=0; i<ppm_factory.channel_count * 2; i++) {
        for(int power=1000; i<=2000; i+=2) {
            update_channel(i, power);
            updates += 1;
        }
    }

    end_time = time(NULL);

    long seconds = end_time - start_time;

    printf("%d updates in %ld seconds\n", updates, seconds);

    destroy();
}
