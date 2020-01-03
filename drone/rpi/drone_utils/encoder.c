#include <stdio.h>
#include <stdlib.h>
#include <signal.h>

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

int start_encoder()
{

    if (gpioInitialise() < 0) return -1;

    gpioSetSignalFunc(SIGINT, stop);


    gpioServo(4, 1300);
    time_sleep(8);
    gpioServo(4, 0);


    gpioTerminate();

    return 0;
}
