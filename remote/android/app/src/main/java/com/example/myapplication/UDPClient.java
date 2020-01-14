package com.example.myapplication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class UDPClient
{

    private InetAddress IPAddress = null;
    private String message = "Hello Android!" ;
    private AsyncTask<Void, Void, Void> async_cient;
    String Message;


    @SuppressLint({"NewApi", "StaticFieldLeak"})
    void NachrichtSenden()
    {
        async_cient = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                DatagramSocket ds = null;

                try
                {
                    InetAddress addr = InetAddress.getLocalHost();
                    ds = new DatagramSocket(5999);
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message.getBytes(), Message.getBytes().length, addr, 5999);
                    ds.send(dp);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}