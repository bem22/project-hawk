#include <time.h>
#include <bits/types/struct_timeval.h>
#include <pigpio.h>

#ifndef SERVER_PPMER_H
#define SERVER_PPMER_H
short GAP;
short NO_WAVES;

struct encoder_struct {
    int channel_count;
    unsigned int gpio;
    int frame_ms;
    int frame_us;
    int frame_s;
    int next_wave_id;
    struct timeval update_time;
    int wave_ids[3];
    gpioPulse_t *waves[3];
    unsigned int *widths;
};

struct encoder_struct ppm_factory;

int init(unsigned int gpio, int channels, int frame_ms);
void update();
pthread_t* ppm_handler_thread;

void update_channel(unsigned int channel, unsigned int width);
void *update_channels();
void destroy();
#endif //SERVER_PPMER_H
