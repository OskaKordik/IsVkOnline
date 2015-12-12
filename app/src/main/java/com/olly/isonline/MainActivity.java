package com.olly.isonline;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    Button button_online;
    EditText inputIDnumber;
    TextView status;
    String ID_NUMBER;
    Boolean isOnline = false;
    String resultJson = "";

    MyTask mt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        inputIDnumber = (EditText) findViewById(R.id.inputIDnumber);
        button_online = (Button) findViewById(R.id.button_online);
        status = (TextView) findViewById(R.id.status);
    }

    public void isOnline(View view) throws IOException {
        ID_NUMBER = inputIDnumber.getText().toString();
        mt = new MyTask();
        mt.execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                connect(ID_NUMBER);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isOnline) {

                JSONObject dataJsonObj;
                String numberStatus;

                try {
                    dataJsonObj = new JSONObject(resultJson);
                    JSONArray response = dataJsonObj.getJSONArray("response");

                    JSONObject onlineStatus = response.getJSONObject(0);
                    numberStatus = onlineStatus.getString("online");
                    if (Integer.parseInt(numberStatus) == 1) status.setText("YES!");
                    else status.setText("NO!");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void connect(String id) throws IOException {

        URL url = new URL("https://api.vk.com/method/users.get?user_ids=" + id + "&fields=online&name_case=Nom&version=5.40");

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            isOnline = true;

            StringBuffer buffer = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                buffer.append(inputLine);
            in.close();
            resultJson = buffer.toString();

        }
    }
}
