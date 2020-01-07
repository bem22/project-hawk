#include <time.h>
#include <bits/types/struct_timeval.h>

#ifndef SERVER_PPMER_H
#define SERVER_PPMER_H
short GAP;
short NO_WAVES;

typedef struct encoder_struct {
    int channel_count;
    unsigned int gpio;
    int frame_ms;
    int frame_us;
    int frame_s;
    int next_wave_id;
    struct timeval update_time;
    int wave_ids[3];
    unsigned int *widths;
} encoderStructy;

struct encoder_struct ppm_factory;

int init(unsigned int gpio, int channels, int frame_ms);
void update();
void update_channel(unsigned int channel, unsigned int width);
void update_channels(unsigned int *widths);
void destroy();
#endif //SERVER_PPMER_H
