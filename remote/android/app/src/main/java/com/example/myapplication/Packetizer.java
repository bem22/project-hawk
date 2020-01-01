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
    private String paramCount;
    public Packetizer() {

        header = "HAWK 1.0";
        chunk = "CHNK:";
        length = "LENGTH:";
        paramCount = "PARAMC:";
    }

    public String packetize(String chunk) {
        StringBuilder sb = new StringBuilder().append("");


        int length = calculatePacketLength(chunk);

        sb.append(this.header).append("\n");
        sb.append(this.chunk).append(chunk).append((chunk.length() == 3) ? " \n" : "\n");
        sb.append(this.length).append(length).append((length < 100) ? " \n" : "\n");
        sb.append(this.paramCount).append(0).append(" \n");

        Log.d("\n", sb.toString());
        return sb.toString();
    }

    public String packetize(String chunk, ArrayList<String> args) {
        StringBuilder sb = new StringBuilder().append("");
        int length = calculatePacketLength(chunk, args);

        sb.append(this.header).append("\n");
        sb.append(this.chunk).append(chunk).append((chunk.length() == 3) ? " \n" : "\n");
        sb.append(this.length).append(length).append((length < 100) ? " \n" : "\n");
        sb.append(this.paramCount).append(args.size()).append((args.size() < 10) ? " \n" : "\n");

        for(int i=0; i<args.size(); i++) {
            sb.append(args.get(i)).append("\n");
        }

        Log.d("\n", sb.toString());
        return sb.toString();
    }

    private int calculatePacketLength(String chunk, ArrayList<String> args) {
        int length = calculatePacketLength(chunk);

        if(args == null) {
            return length;
        }

        for (int i = 0; i < args.size(); i++) {
            length += args.get(i).length() + 1;
        }

        return length;
    }

    private int calculatePacketLength(String chunk) {
        int length = 0;

        length += this.header.length() + 1;
        length += this.chunk.length() + 4 + 1;
        length += this.length.length() + 3 + 1;
        length += this.paramCount.length() + 2 + 1;

        return length;
    }

}
