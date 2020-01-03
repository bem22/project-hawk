#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <pigpio.h>

#define NUM_GPIO 32

#define MIN_WIDTH 1000
#define MAX_WIDTH 2000

int run=1;

int step[NUM_GPIO];
int width[NUM_GPIO];
int used[NUM_GPIO];

void stop(int signum)
{
    run = 0;
}

int start_encoder(int value)
{


    gpioSetSignalFunc(SIGINT, stop);

    while(run) {
        gpioServo(4, value);
        usleep(5);
    }


    return 0;
}
