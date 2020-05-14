package com.melihcelenk.chatclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GirisActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    ScrollView scrollView;
    EditText serverIPET;
    TextView serverIPTV;
    EditText kullaniciAdiET;
    EditText parolaET;
    EditText textField;
    TextView messageArea;
    Button serverButton;
    Button gonderButton;
    EditText hedefET;
    Button hedefButton;
    static int donguHedefKontrol;

    String destinationName;

    BufferedReader in;
    PrintWriter out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        scrollView = findViewById(R.id.scrollView2);
        serverIPET = findViewById(R.id.serverIPET);
        serverIPTV = findViewById(R.id.serverIPTV);
        kullaniciAdiET = findViewById(R.id.kullaniciAdiET);
        parolaET = findViewById(R.id.parolaET);
        serverButton = findViewById(R.id.serverButton);
        gonderButton = findViewById(R.id.gonderButton);
        textField = findViewById(R.id.textField);
        messageArea = findViewById(R.id.messageArea);
        linearLayout = findViewById(R.id.linearLayout);
        hedefET = findViewById(R.id.hedefET);
        hedefButton = findViewById(R.id.hedefButton);

        serverIPET.setText(getServerAddress());
        kullaniciAdiET.setText(getName());
        parolaET.setText(getPass());


        donguHedefKontrol = 0;

        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setServerAddress(serverIPET.getText().toString());
                setName(kullaniciAdiET.getText().toString());
                setPass(parolaET.getText().toString());
                giris();
            }
        });
        hedefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //donguHedefKontrol = 0;
                destinationName = hedefET.getText().toString();

            }
        });
        gonderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        textField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //scrollView.fullScroll(View.FOCUS_DOWN);
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 200);
                return false;
            }
        });

    }



    private void setServerAddress(String serverIP) {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("serverIP",serverIP);
        editor.commit();
    }
    private String getServerAddress() {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPref",Context.MODE_PRIVATE);
        String kaydedilmisIP = sharedPref.getString("serverIP","192.168.0.102");
        return kaydedilmisIP;
    }
    private void setName(String name) {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name",name);
        editor.commit();
    }
    private String getName() {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPref",Context.MODE_PRIVATE);
        String kaydedilmisIsim = sharedPref.getString("name","");
        return kaydedilmisIsim;
    }
    private void setPass(String pass) {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("pass",pass);
        editor.commit();
    }
    private String getPass() {
        SharedPreferences sharedPref = this.getSharedPreferences("sharedPref",Context.MODE_PRIVATE);
        String kaydedilmisParola = sharedPref.getString("pass","");
        return kaydedilmisParola;
    }
    private String getDestinationName() {
        if(destinationName!=null) return destinationName;
        else return "";
    }
    private void appendResultsText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageArea.append(text + "\n");
            }
        });
    }
    private void sendMessage(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(!textField.getText().toString().isEmpty()){
                    out.println(textField.getText().toString());
                }

            }
        });
        thread.start();

        textField.setText("");
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 200);
    }
    private void hedefEkraninaGec(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverIPET.setVisibility(View.GONE);
                serverIPTV.setVisibility(View.GONE);
                serverButton.setVisibility(View.GONE);
                kullaniciAdiET.setVisibility(View.GONE);
                parolaET.setVisibility(View.GONE);
                hedefET.setVisibility(View.VISIBLE);
                hedefButton.setVisibility(View.VISIBLE);
            }
        });

    }

    private void mesajlaraGec(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hedefET.setVisibility(View.GONE);
                hedefButton.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                messageArea.setVisibility(View.VISIBLE);
                textField.setVisibility(View.VISIBLE);
                gonderButton.setVisibility(View.VISIBLE);
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 300);
            }
        });
    }

    private void giris(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Make connection and initialize streams
                String serverAddress = getServerAddress();
                Socket socket = null;
                try {
                    socket = new Socket(serverAddress, 9001);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String name="";
                String pass="";

                int donguKontrol = 0;
                // Protokole göre sunucudan gelen tüm mesajları işler.
                while (true) {
                    System.out.println("Dönüyor...");
                    String line = null;
                    try {
                        line = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //xxxxxxxxxxxxxxxxxxxxx
                    System.out.println(line);
                    if (line.startsWith("SUBMITNAME")) {
                        name=getName();
                        if(donguKontrol>3) break;
                        out.println("KULLANICIADI "+name);
                        donguKontrol++;
                    }
                    else if (line.startsWith("SUBMITPASS")) {
                        pass=getPass();
                        if(donguKontrol>3) break;
                        out.println("PAROLA "+pass);
                        donguKontrol++;
                    }
                    else if (line.startsWith("SUBMITDESTINATIONNAME")) {

                        if(donguHedefKontrol<2) {
                            hedefEkraninaGec();
                        }
                        donguHedefKontrol++;
                        out.println(getDestinationName());
                    }
                    else if (line.startsWith("NAMEACCEPTED")) {
                        mesajlaraGec();
                    }
                    else if (line.startsWith("MESSAGE " + destinationName) || line.startsWith("MESSAGE " + name) ) {
                        appendResultsText(line.substring(8) + "\n");
                        AsagiIn();
                    }
                    else if (destinationName.equals("server") && line.startsWith("MSGALL ")) {
                        appendResultsText(line.substring(7) + "\n");
                        AsagiIn();
                    }
                    else if (line.startsWith("MSGGRP ")) {
                        String[] x = line.substring(7).split(" ", 2);
                        if(x[0].equals(destinationName)){
                            appendResultsText(x[1]+"\n");
                            AsagiIn();
                        }
                    }
                    else{
                    }
                }
            }
        });
        thread.start();


    }
    private void AsagiIn(){
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 200);
    }
}


