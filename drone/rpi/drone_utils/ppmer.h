#include <time.h>
#include <bits/types/struct_timeval.h>

#ifndef SERVER_PPMER_H
#define SERVER_PPMER_H

const short GAP = 300;
const short NO_WAVES = 3;

typedef struct encoder_struct {
    int channel_count;
    unsigned int gpio;
    int frame_ms;
    int frame_us;
    int frame_s;
    int next_wave_id;
    time_t update_time;
    int wave_ids[3];
    unsigned int widths[18];
} encoderStructy;

struct encoder_struct ppm_factory;

#endif //SERVER_PPMER_H