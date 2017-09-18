
package com.serban.cfit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    EditText FirstNameView;
    TextView tv;
    String FirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creeaza campurile edittext
        FirstNameView = (EditText) findViewById(R.id.FirstName);
        tv = (TextView) findViewById(R.id.textView);

        Button b1 = (Button) findViewById(R.id.senddata);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senddatatoserver(v);
            }
        });

    }

    public void senddatatoserver(View v) {

        //preia datele din edittext (le converteste in string)
        FirstName = FirstNameView.getText().toString();
        //creeaza un obiect tip json
        JSONObject post_dict = new JSONObject();

        try {
            //plaseaza campurile edittext in json
            post_dict.accumulate("id", 12);
            post_dict.accumulate("firstName", "gelu");
            post_dict.accumulate("lastName", "paralelu");
            post_dict.accumulate("email", "lalal");
            post_dict.accumulate("mobile", "puiimei");
            post_dict.accumulate("dateOfBirth", 1504047);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //verifica daca obiectul JSON are campurile preluate mai sus si daca da obiectul de tip json este transmis catre server

        if (post_dict.length() > 0) {
            new SendJsonDataToServer(this.getApplicationContext()).execute(String.valueOf(post_dict));
        }
    }


    private class SendJsonDataToServer extends AsyncTask<String,String,String> {
        private Context context;
        //metoda doInBackground are rolul de a prelucra informatiile trimise catre server
        public SendJsonDataToServer(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            final String TAG = "tag";
            String JsonResponse = null;
            String JsonDATA = params[0]; //aici retinem datele preluate prin json
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://10.90.5.114:8080/Spring4Rest/customers"); //url server
                urlConnection = (HttpURLConnection) url.openConnection(); //cerere incepere conexiune
                //urlConnection.setDoOutput(true); //face request-ul POST
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                //decomenteaza chestiile astea ca sa scrii pe server
                //Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                //writer.write(JsonDATA); //scriem datele preluate prin json pe server
                //writer.close(); //incheiem procesul de scriere
                InputStream inputStream = urlConnection.getInputStream(); //citire date pe url
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                } //verificare daca datele preluate prin json au fost trimise catre server
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;

                int cnt = 0;

                while ((inputLine = reader.readLine()) != null) {
                    cnt++;
                    buffer.append(inputLine + " " + cnt);
                }

                if (buffer.length() == 0) {
                    return null;
                } //citire date de pe server
                JsonResponse = buffer.toString();
                Log.i(TAG,JsonResponse);
                //send to post execute
                return JsonResponse;

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
        protected void onPostExecute(String message) {
            if (message == null) {
                tv.setText("NULL");
            } else {
                tv.setText(message);
            }
        }
    }
}




