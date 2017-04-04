package com.sss.carolina.bluetooth.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sss.carolina.bluetooth.network.Data.SocketData;
import com.sss.carolina.bluetooth.network.Data.WatchData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by carolina on 03.04.17.
 */

public class ServSocket extends AsyncTask<WatchData, Integer, Integer> {

    private static String ip;
    private static int port;
    private static Socket sock;
    Context mCtx;
    Socket mySock;


    public ServSocket(String ipAddress, int portParam) {
        this.ip = ipAddress;
        this.port = portParam;
    }


    @Override
    protected Integer doInBackground(WatchData... param) {
        InetAddress serverAddress;
        mCtx = param[0].ctx;
        String requestMess = param[0].mess;
        try {
            while (true) {
                serverAddress = InetAddress.getByName(ip);
                sock = new java.net.Socket(serverAddress, port);


                SocketData data = new SocketData();
                data.ctx = mCtx;
                data.socket = mySock;

                GetPacket pack = new GetPacket();
                AsyncTask<SocketData, Integer, Integer> running = pack.execute(data);

                String message = requestMess;

                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mySock.getOutputStream())), true);

                    out.println(message);

                } catch (Exception e) {
                }
                // Следим за потоком, принимающим сообщения
                while (running.getStatus().equals(AsyncTask.Status.RUNNING)) {

                }
                // Если поток закончил принимать сообщения - это означает,
                // что соединение разорвано (других причин нет).
                // Это означает, что нужно закрыть сокет
                // и открыть его опять в бесконечном цикле (см. while(true) выше)
                try {
                    mySock.close();
                } catch (Exception e) {
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}

class GetPacket extends AsyncTask<SocketData, Integer, Integer> {
    Context mCtx;
    char[] mData;
    Socket mySock;

    @Override
    protected Integer doInBackground(SocketData... param) {
        mySock = param[0].socket;
        mCtx = param[0].ctx;
        mData = new char[4096];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mySock.getInputStream()));
            int read = 0;

            // Принимаем сообщение от сервера
            // Данный цикл будет работать, пока соединение не оборвется
            // или внешний поток не скажет данному cancel()
            while ((read = reader.read(mData)) >= 0 && !isCancelled()) {
                // "Вызываем" onProgressUpdate каждый раз, когда принято сообщение
                if (read > 0) publishProgress(read);
            }
            reader.close();
        } catch (IOException e) {
            return -1;
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        try {
            // Получаем принятое от сервера сообщение
            String prop = String.valueOf(mData);
            // Делаем с сообщением, что хотим. Я, например, пишу в базу
            Intent intent = new Intent();
            intent.putExtra("prop", prop);
        } catch (Exception e) {
            Toast.makeText(mCtx, "Socket error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled(Integer integer) {

    }

}
