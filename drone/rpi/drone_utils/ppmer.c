#include "ppmer.h"
#include "state.h"
#include "../network_utils/connection.h"
#include <pigpio.h>
#include <sys/time.h>
#include <malloc.h>
#include <asm/errno.h>
#include <errno.h>
#include <zconf.h>
#include <string.h>

int init(unsigned int gpio, int channels, int frame_ms) {
    GAP = 300;
    NO_WAVES = 3;

    if (gpioInitialise() < 0) return 0;

    // Initialize all the variables in the PPM encoder structure
    ppm_factory.gpio = gpio;
    ppm_factory.channel_count = channels;
    ppm_factory.frame_ms = frame_ms;
    ppm_factory.frame_us = frame_ms * 1000;
    ppm_factory.frame_s = frame_ms / 1000;

    ppm_factory.widths = malloc(sizeof(int) * channels);

    for(int i=0; i<3; i++) {
        ppm_factory.waves[i] = malloc(6 * (channels + 1) * sizeof(gpioPulse_t));
    }
    // Set the pulse width of each channel to 1000
    for(int i=0; i<channels; i++) {
        ppm_factory.widths[i] = 1000;
    }

    // Set the wave ids to control value
    for(int i=0; i<NO_WAVES; i++) {
        ppm_factory.wave_ids[i] = -1;
    }

    // Set wave id counter
    ppm_factory.next_wave_id = 0;

    // Set GPIO pin to ground
    gpioWrite(gpio, PI_LOW);


    // Set the update time
    gettimeofday(&ppm_factory.update_time, NULL);
    ppm_handler_thread = (pthread_t*) malloc(sizeof(pthread_t));
    return 0;
}

void update() {
    uint32_t  micros = 0;
    int i, wave_id;
    struct timeval remaining_time;

    // Set the first channel_count * 2 elements in the wave pulse (i.e lows and highs)
    for(i=0; i < ppm_factory.channel_count*2; i+=2) {
        ppm_factory.waves[ppm_factory.next_wave_id][i] = (gpioPulse_t){1u << ppm_factory.gpio, 0, GAP};
        ppm_factory.waves[ppm_factory.next_wave_id][i+1] = (gpioPulse_t){0, 1u << ppm_factory.gpio, ppm_factory.widths[i/2] - GAP};
        micros+=ppm_factory.widths[i];
    }

    // Fill with ground for the remaining time of the frame
    ppm_factory.waves[ppm_factory.next_wave_id][i] = (gpioPulse_t){1u << ppm_factory.gpio, 0, GAP};
    micros+=GAP;
    ppm_factory.waves[ppm_factory.next_wave_id][++i] =
            (gpioPulse_t){0, 1u << ppm_factory.gpio, ppm_factory.frame_us - micros};

    // Create the wave
    gpioWaveAddGeneric(18, ppm_factory.waves[ppm_factory.next_wave_id]);
    wave_id = gpioWaveCreate();

    // Send the wave to the wire
    gpioWaveTxSend(wave_id, PI_WAVE_MODE_REPEAT_SYNC);

    // Save the wave id in the array and increment the counter
    ppm_factory.wave_ids[ppm_factory.next_wave_id++] = wave_id;

    // Reset the counter when it reaches NO_WAVES
    if(ppm_factory.next_wave_id >= NO_WAVES) { ppm_factory.next_wave_id = 0; }

    struct timeval current_time;
    gettimeofday(&current_time, NULL);
    remaining_time.tv_sec = ppm_factory.update_time.tv_sec + ppm_factory.frame_s - current_time.tv_sec;
    remaining_time.tv_usec = ppm_factory.update_time.tv_usec + ppm_factory.frame_us - current_time.tv_usec;

    // Sleep for the remaining time
    if(remaining_time.tv_usec > 0) {
        fflush(stdout);
        gpioSleep(PI_TIME_RELATIVE, 0, remaining_time.tv_usec);
    }

    // Update the timer
    gettimeofday(&ppm_factory.update_time, NULL);

    // Get next wave_id
    wave_id = ppm_factory.wave_ids[ppm_factory.next_wave_id];

    // Delete the next wave (by id) and clear the wave_id in the wave reference array
    if(wave_id != -1) {
        gpioWaveDelete(wave_id);
        ppm_factory.wave_ids[ppm_factory.next_wave_id] = -1;
        //memset(ppm_factory.waves[ppm_factory.next_wave_id], 0 , (ppm_factory.channel_count + 1) * 2);
    }
}

void *update_channels() {

    while(!drone_state.ARMED && connected) {

        sleep(1);
        while (drone_state.ARMED) {
            ppm_factory.widths[0] = drone_state.PITCH;
            ppm_factory.widths[1] = drone_state.YAW;
            ppm_factory.widths[2] = drone_state.THROTTLE;
            ppm_factory.widths[3] = drone_state.ROLL;
            ppm_factory.widths[4] = drone_state.AUX1;
            ppm_factory.widths[5] = drone_state.AUX2;
            //ppm_factory.widths[6] = drone_state.AUX3;
            //ppm_factory.widths[7] = drone_state.AUX4;
            printf("%d %d %d %d %d %d\n", ppm_factory.widths[0], ppm_factory.widths[1], ppm_factory.widths[2], ppm_factory.widths[3], ppm_factory.widths[4], ppm_factory.widths[5]);
            update();
        }
    }
}

void destroy() {
    // Stop the current wave
    gpioWaveTxStop();

    // Clean all waves and wave_ids
    for(int i=0; i < NO_WAVES; i++) {
        if (ppm_factory.wave_ids[i] != -1) {
            free(ppm_factory.waves[i]);
            gpioWaveDelete(ppm_factory.wave_ids[i]);
            ppm_factory.wave_ids[i] = -1;
        }
    }
    gpioTerminate();

    free(ppm_handler_thread);
}