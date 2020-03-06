//
// Created by bem22 on 3/4/20.
//

#include "packetizer.h"
char* protocolHeader = "HAWK 1.0 RSP\n";
int protocolHeaderLength = 13;

char* protocolChunkTag = "CHNK:";
int protocolChunkTagLength = 5;

char* protocolLengthTag = "LENGTH:";
int protocolLengthTagLength = 7;

char* protocolParamCountTag = "PARAMC:";
int protocolParamCountTagLength = 7;

int protocolChunkValueLength = 4;

char protocolLengthValue[5];
int protocolLengthValueLength = 4;

char protocolParamCountValue[2];
char protocolParamCountValueLength = 2;

char protocolPayloadLength = 4;

char packet[1024];

char* calculatePacketLength(int paramCount) {
    // TODO: Calculate packet length here
    int size = 99;

    if(size < 100) {
        sprintf(protocolLengthValue, "%d  ", size);
    } else if(size > 999) {
        sprintf(protocolLengthValue, "%d ", size);
    }

    return protocolLengthValue;
}

char* getParamCountString(int paramCount) {
    if(paramCount < 10) {
        sprintf(protocolParamCountValue, "%d ", paramCount);
    } else {
        sprintf(protocolParamCountValue, "%d", paramCount);
    }

    return protocolParamCountValue;

}

char* buildPacket(char* chunk, int paramCount, int payloadLength, char* payload) {
    memset(packet, '\0', 1024);

    int cursor = 0;

    strncpy(packet + cursor, protocolHeader, protocolHeaderLength);
    cursor += protocolHeaderLength;

    strncpy(packet + cursor, protocolChunkTag, protocolChunkTagLength);
    cursor += protocolChunkTagLength;
    strncpy(packet + cursor, chunk, protocolChunkValueLength);
    cursor += protocolChunkValueLength;
    strncpy(packet + cursor, "\n", 1);
    cursor ++;

    strncpy(packet + cursor, protocolLengthTag, protocolLengthTagLength);
    cursor += protocolLengthTagLength;
    strncpy(packet + cursor, calculatePacketLength(paramCount), protocolLengthValueLength);
    cursor += protocolLengthValueLength;
    strncpy(packet + cursor, "\n", 1);
    cursor ++;

    strncpy(packet + cursor, protocolParamCountTag, protocolParamCountTagLength);
    cursor += protocolParamCountTagLength;
    strncpy(packet + cursor, getParamCountString(paramCount), protocolParamCountValueLength);
    cursor += protocolParamCountValueLength;
    strncpy(packet + cursor, "\n", 1);
    cursor ++;

    strncpy(packet + cursor, payload, payloadLength);
    cursor += payloadLength;
    strncpy(packet + cursor, "\n", 1);
    cursor ++;

    //TODO: Add params here

    return packet;
}
