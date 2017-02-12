package info.management;

/*
MainActivity.java
Scopia Management API Client Sample / written by Yosuke Sawamura on 2017/02/04.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.util.Base64;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public String TAG = getClass().getName();
    public int enable_debugLog = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String req_msg_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
            "<MCU_XML_API>" +
            "<Version>iCM 5.0</Version>" +
            "<Request>" +
            "<Get_User_Request>" +
            "<RequestID>1</RequestID>" +
            "</Get_User_Request>" +
            "</Request>" +
            "</MCU_XML_API>";

        final String req_msg_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
             "<MCU_XML_API>" +
            "<Version>iCM 5.0</Version>" +
            "<Request>" +
            "<Get_Gatekeeper_Request>" +
            "<RequestID>@reqID</RequestID>" +
            "</Get_Gatekeeper_Request>" +
            "</Request>" +
            "</MCU_XML_API>";

        final Button Btn01 = (Button) findViewById(R.id.btn01);
        final EditText EditUser = (EditText) findViewById(R.id.edit_username);
        final EditText EditPassword = (EditText) findViewById(R.id.edit_password);
        final EditText EditSever = (EditText) findViewById(R.id.edit_server_ip);
        final EditText EditServerPort = (EditText) findViewById(R.id.edit_server_port);

        Btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugLog("onClick: Btn01");
                String un = EditUser.getText().toString();
                String pw = EditPassword.getText().toString();
                String s_ip = EditSever.getText().toString();
                String s_port = EditServerPort.getText().toString();
                String server_address = "http://" + s_ip + ":" + s_port + "/xmlservice/entry";
                String cred = un + ":" + pw;
                String enc_cred = Base64.encodeToString(cred.getBytes(), Base64.DEFAULT);

                sendMessage(server_address, enc_cred, req_msg_1);
            }
        });

        final Button Btn02 = (Button) findViewById(R.id.btn02);
        Btn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugLog("onClick: Btn02");
                String un = EditUser.getText().toString();
                String pw = EditPassword.getText().toString();
                String s_ip = EditSever.getText().toString();
                String s_port = EditServerPort.getText().toString();
                String server_address = "http://" + s_ip + ":" + s_port + "/xmlservice/entry";
                String cred = un + ":" + pw;
                String enc_cred = Base64.encodeToString(cred.getBytes(), Base64.DEFAULT);

                sendMessage(server_address, enc_cred, req_msg_2);
            }
        });
    }

    public void debugLog(String string){
        if (enable_debugLog==1){
            Log.d(TAG, string);
        }
    }

    public void printResult(String strings){
        final TextView text = (TextView) findViewById(R.id.response);
        text.setText(strings.replace("><", ">\n<"));
    }

    public void sendMessage(final String url, String credential, String msg) {
        final String server_url = url;
        final String enc_credential = credential;
        final String req_msg = msg;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                StringBuffer builder = new StringBuffer("");
                try{
                    URL url = new URL(server_url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST"); // CAN BE "POST" "GET" ...
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Basic " + enc_credential);

                    byte[] outputInBytes = req_msg.getBytes("UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write( outputInBytes );
                    os.close();

                    connection.connect();

                    InputStream iS = connection.getInputStream();

                    BufferedReader bR = new BufferedReader(new InputStreamReader(iS, "UTF-8"));
                    String line = "";
                    while ((line = bR.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return builder.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                printResult(result);
                debugLog(result);
            }
        }.execute();
    }

}
