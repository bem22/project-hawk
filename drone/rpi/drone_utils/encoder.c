#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <pigpio.h>

#define GAP 300
#define CHANNEL_COUNT 8
#define FRAME_MS 27

uint8_t widths[8] = {1000};

int run=1;

void stop(int signum)
{
    run = 0;
}

int start_encoder(int value)
{

    gpioServo(4, value);

    return 0;
}

int pulse_train(int[6] pulses) {

}
