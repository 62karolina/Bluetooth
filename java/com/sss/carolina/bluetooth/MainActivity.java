package com.sss.carolina.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sss.carolina.bluetooth.network.Data.WatchData;
import com.sss.carolina.bluetooth.network.ServSocket;

import org.w3c.dom.Text;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText ipText, portText;
    Button letsgoButt;
    String mess;
    TextView answerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipText = (EditText)findViewById(R.id.ipText);
        portText = (EditText)findViewById(R.id.portText);
        answerText = (TextView)findViewById(R.id.answerText);
        letsgoButt = (Button)findViewById(R.id.letgoButt);

        letsgoButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Log.d("Information: ", "Server is running");
                startService(ipText.getText().toString(), Integer.parseInt(portText.getText().toString()));
                Intent intent = getIntent();
                String answer = intent.getStringExtra("prop");
                answerText.setText(answer);
            }
        });



    }

    // Здесь выполняем инициализацию нужных нам значений
    // и открываем наше сокет-соединение
    private void startService(String ip, int portT) {

        mess = "hello";
        Log.d("Information: ", "Message has been send");

        try {
            openConnection(ip, portT);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // данный метод открыает соединение
    public void openConnection(String ip, int portT) throws InterruptedException
    {
        Log.d("Information ", "Open connection");
        try {

        // WatchData - это класс, с помощью которого мы передадим параметры в
        // создаваемый поток
            WatchData data = new WatchData();
            data.mess = mess;
            data.ctx = this;


            // создаем новый поток для сокет-соединения

            new ServSocket(ip, portT).execute(data);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


