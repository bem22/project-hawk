#include <string.h>

void replace(char* source, char target, char value) {
    for(int i=0; i<strlen(source); i++) {
        if(source[i] == target) {
            source[i] = value;
        }
    }
}