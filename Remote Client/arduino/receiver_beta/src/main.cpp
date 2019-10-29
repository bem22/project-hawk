#include <Arduino.h>
// 3 channels 4 timers
unsigned long ch[3], t[4];

// Counter for pulses (which pulse at which time?)
int pulse = 0;

void setup() {
	// Turn on interrupts
	PCICR |= (1 << PCIE0);

	// Attatch interrupt on INT0 or D8
	PCMSK0 |= (1 << PCINT0);
}

void loop() {
  // put your main code here, to run repeatedly:
}

// Interrupt service routine
ISR(PCINT0_vect) {
	// Digital read high
	if (PINB & B00000001) {

		// Take the system time 
		t[pulse] = micros();

		// Check which pulse is and execute the code in the switch case
		switch (pulse) {
			case 1:
			ch[1] = t[1] - t[0];
			pulse++;
			if(ch[1] > 3000) {
				ch[0] = t[1];
				pulse = 1;
			}
			break;
			case 2:
			ch[2] = t[2] - t[1];
			pulse++;
			if(ch[1] > 3000) {
				ch[0] = t[1];
				pulse = 1;
			}
			break;
			case 3:
			ch[0] = t[2] - t[1];
			pulse++;
			if(ch[1] > 3000) {
				ch[0] = t[1];
				pulse = 1;
			}
			break;
			default:
				pulse++;
			break;
		}
	}
}