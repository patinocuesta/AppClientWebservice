package com.example.appclientwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void envoi(View view) {
        Asynchrone as = new Asynchrone();
        as.execute("http://10.0.2.2/webservice/webservice.php");
    }
    public class Asynchrone extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;
            String resultat = null;
            int responseCode = 0;
            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                //on specifie la commande http
                httpURLConnection.setRequestMethod("POST");
                //on specifie le codage
                httpURLConnection.setRequestProperty("Accept-Language", "UTF-8");
                //JSON a traiter comme String
                String body="{\"title\":\"les maths 4\",\"author\":\"pascal\"}";
                //On envoie des données dans le body
                httpURLConnection.setDoOutput(true);
                //OutputStreamWriter est un pont entre les flux de caractères et les flux d'octets
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
                //On ecrit les données STRING sur le flux d'ecriture
                outputStreamWriter.write(body);
                //On valide l'ecriture
                outputStreamWriter.flush();
                responseCode=httpURLConnection.getResponseCode();
                resultat= String.valueOf(responseCode);
                outputStreamWriter.close();
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultat;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
        }
    }
    //On ajoute une exception à la signature lorsque on pense que on n'aura qu'un seul type de exception
    private void parsejsonFile(String jString) throws JSONException {
        //On va creer un objet JSON a partir d'une chaine*
        JSONObject jsonObject = new JSONObject(jString);
        //On recuper le JSON
        JSONObject livres=jsonObject.getJSONObject("livres");
        //On recupere l'Array de JSON
        JSONArray livre=livres.getJSONArray("livre");
        //on va parcourir l'array de json et recuperer les elements
        for (int i=0; i<livre.length();i++){
            //on recupere le Ieme obj JSON
            String id=livre.getJSONObject(i).getString("id");
            Log.d("JSONObject", "id: "+id);
            String title=livre.getJSONObject(i).getString("title");
            Log.d("JSONObject","title: "+ title);
            String author=livre.getJSONObject(i).getString("author");
            Log.d("JSONObject", "author: "+ author);
        }
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
       /* @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;
            String resultat = null;
            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream= new BufferedInputStream(httpURLConnection.getInputStream());
                int inChar;
                StringBuilder readStr = new StringBuilder();
                while((inChar=inputStream.read())!=-1){
                    readStr.append((char) inChar);
                }
                resultat=readStr.toString();
                //on passe resultat à une methode qui va convertir la chaine en json
                //et parser le json
                parsejsonFile(resultat);
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return resultat;
        }*/
////////////////////////////////////////////////////////////////////////////////////////////////////
