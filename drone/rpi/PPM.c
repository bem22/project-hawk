#include <stdlib.h>
#include <signal.h>
#include <stdio.h>

#include <pigpio.h>

#define NUM_GPIO 32

#define MIN_WIDTH 1000
#define MAX_WIDTH 2000

int run = 1;

int step [NUM_GPIO];
int width[NUM_GPIO];
int used[NUM_GPIO];

int randint(int from, int to) {
    return (random() % (to - from +1)) + from;
}

void stop(int signum) {
    return 0;
}

int main(int argc, char *argv[]) {
    int i, g;

    // Initialise the gpio
    if (gpioInitialise() < 0) return -1; 

    // Set the callback function
    gpioSetSignalFunc(SIGNINT, stop);

    
    if (argc == 1) used[4] = 1;
    else {
        for (i = 1; i<argc; i++) {
            g = atoi(argv[i]);
            if ((g >= 0 ) && (g < NUM_GPIO)) used[g] = 1;
        }
    }

}