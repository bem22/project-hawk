package com.example.myapplication;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Packetizer{

    private String header;
    private String chunk;
    private String length;

    public Packetizer() {

        header = "HAWK 1.0\n";
        chunk = "CHNK: ";
        length = "LENGTH: ";
    }

    public String packetize(String chunk) {

        String packet = "";

        packet += this.header;
        packet += this.chunk + chunk + "\n";
        packet += this.length + calculatePacketLength(chunk);

        return packet;
    }

    public String packetize(String chunk, ArrayList<String> args) {
        String packet = "";

        packet += this.header;
        packet += this.chunk + chunk + "\n";
        packet += this.length + calculatePacketLength(chunk, args);
        Log.d("packet is", packet);
        return packet;
    }

    private int calculatePacketLength(String chunk, ArrayList<String> args) {
        int length = calculatePacketLength(chunk);

        if(args == null) {
            return length;
        }

        for (int i = 0; i < args.size(); i++) {
            length += args.get(i).length();
        }

        return length;
    }

    private int calculatePacketLength(String chunk) {
        int length = 0;

        length += this.header.length();
        length += this.chunk.length();
        length += this.length.length();
        length += chunk.length();


        return length;
    }

}
