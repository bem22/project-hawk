#include "ppmer.h"
#include <pigpio.h>
#include <sys/time.h>
#include <malloc.h>
#include <asm/errno.h>
#include <errno.h>

int microsleep(long tms)
{
    struct timespec ts;
    int ret;

    if (tms < 0)
    {
        errno = EINVAL;
        return -1;
    }

    ts.tv_sec = tms / 1000;
    ts.tv_nsec = (tms % 1000) * 1000000;

    do {
        ret = nanosleep(&ts, &ts);
    } while (ret && errno == EINTR);

    return ret;
}

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

    unsigned int *widths = malloc(sizeof(int) * 18);

    ppm_factory.widths = widths;

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

    return 0;
}

void update() {
    uint32_t  micros = 0;
    int i, wave_id;
    struct timeval remaining_time;

    // Declare an array of pulses
    gpioPulse_t waveform[ppm_factory.channel_count * 2 + 2];

    // Set the first channel_count * 2 elements in the wave pulse (i.e lows and highs)
    for(i=0; i < ppm_factory.channel_count*2; i+=2) {
        waveform[i] = (gpioPulse_t){1u << ppm_factory.gpio, 0, GAP};
        waveform[i+1] = (gpioPulse_t){0, 1u << ppm_factory.gpio, ppm_factory.widths[i/2] - GAP};
        micros+=ppm_factory.widths[i];
    }

    // Fill with ground for the remaining time of the frame
    waveform[i] = (gpioPulse_t){1u << ppm_factory.gpio, 0, GAP};
    micros+=GAP;
    waveform[++i] =
            (gpioPulse_t){0, 1u << ppm_factory.gpio, ppm_factory.frame_us - micros};

    // Create the wave
    gpioWaveAddGeneric(i, waveform);
    wave_id = gpioWaveCreate();

    // Send the wave to the wire
    gpioWaveTxSend(wave_id, PI_WAVE_MODE_REPEAT_SYNC);

    // Save the wave id in the array and increment the counter
    ppm_factory.wave_ids[ppm_factory.next_wave_id++] = wave_id;

    // Reset the counter when it reaches NO_WAVES
    if(ppm_factory.next_wave_id >= NO_WAVES) { ppm_factory.next_wave_id = 0; }

    // TODO: This time calculus might crash
    struct timeval current_time;
    gettimeofday(&current_time, NULL);
    remaining_time.tv_usec = ppm_factory.update_time.tv_usec + ppm_factory.frame_us - current_time.tv_usec;

    // Sleep for the remaining time
    if(remaining_time.tv_usec > 0) {
        microsleep(remaining_time.tv_usec);
    }

    // Update the timer
    gettimeofday(&ppm_factory.update_time, NULL);

    // Get next wave_id
    wave_id = ppm_factory.wave_ids[ppm_factory.next_wave_id];

    // Delete the next wave (by id) and clear the wave_id in the wave reference array
    if(wave_id != -1) {
        gpioWaveDelete(wave_id);
        ppm_factory.wave_ids[ppm_factory.next_wave_id] = -1;
    }
}

void update_channel(unsigned int channel, unsigned int width) {
    ppm_factory.widths[channel] = width;
    update();
}

void update_channels(unsigned int *widths) {
    ppm_factory.widths = widths;
    update();
}

void destroy() {
    // Stop the current wave
    gpioWaveTxStop();

    // Clean all waves and wave_ids
    for(int i=0; i < NO_WAVES; i++) {
        if (ppm_factory.wave_ids[i] != -1) {
            gpioWaveDelete(ppm_factory.wave_ids[i]);
            ppm_factory.wave_ids[i] = -1;
        }
    }

    gpioTerminate();
}