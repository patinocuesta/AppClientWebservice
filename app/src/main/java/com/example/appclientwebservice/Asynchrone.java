package com.example.appclientwebservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Asynchrone extends AsyncTask<String,Integer, ArrayList<Livre>>
{ //etape 0
    // le 1er param  String: c'est ce qu'on envoit à doInBackGround (c'est la connection)
    // ArrayList<Livre> c'est celui que renvoit, (ici: la liste des livres)
    //aficher le resultat et parser le json
    // etape 1: quand on se situe dans une classe où on n'arrive pas à récupérer un context,
    // on peut définir un objet de type Activity, c'est ce qu'on va utiliser:
    private String action=null; // déclaration , connu dans doInBackGround
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    // on va initialiser un constructeur: par generate => constructor
    Asynchrone(Activity activity) { this.activity = activity; }
    @Override
    protected ArrayList<Livre> doInBackground(String... strings) {
        // on initialise un ArrayListe de Livre destiné à récupérer l'ensemble des livres.
        //via la méthode GET et qui sera renvoyé à la méthode onPostexecute
        ArrayList<Livre> liste = new ArrayList<Livre>();
        // On récupère à partir du tableau strings l'url est la connection,
        // l'action, le livre, l'id à supprimer
        String connection=strings[0];
        action=strings[1]; // déclaré en global
        String livre=strings[2];
        int id= Integer.parseInt(strings[3]);
        // Dans cette méthode, on va appeler une des méthodes de mise-à-jour
        // (Post, Update, Delete ou de lecture (get)
        // en fonction d'une action(Insert, Update,Delete, Read)
        if (action.equals("GET")){
            liste=getAllLivres(connection);
            MainActivity.livres=liste;
            for (Livre s1:liste){
                Log.d("Loading titles",s1.toString());
            }
        }
        if (action.equals("POST")) insertLivre(connection,livre);
        if (action.equals("PUT")) updateLivre(connection,livre);
        if (action.equals("DELETE")) deleteLivre(connection,id);
        if (action.equals("GET")) getAllLivres(connection);
        return liste;
    }// fin de doInBackground
    @Override
    protected void onPostExecute(ArrayList<Livre> s) {
        // s est l'ArrayList<Livre>
        super.onPostExecute(s);
        // dans le cas où action = GET, on doit afficher la liste des livres
        //dans une ListView à partir des Données envoyées par la méthode doInBackGround
        //sinon on ne fait rien.
        //la classe AsyncTask n'est pas une activity, ne donnera pas de context voir etape 1
        //on va definir un ArrayA après avoir créé danslayout pour la listView:
        if (action.equals("GET")) {
            ArrayAdapter<Livre> aa = new ArrayAdapter<Livre>(this.activity,
                    android.R.layout.simple_list_item_single_choice, s);
            //récupéerer la liste: le find id ne marche pas comme context ==>
            ListView listView = activity.findViewById(R.id.liste);
            //affecter à la liste
            listView.setAdapter(aa);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // une constante statique
            //avec le GET pas de pb; mais pas avec POST, PUT, DELETE ==> voir plus haut
            //doInBackGround
            //==> une autre activity
        }
        //Toast.makeText(this.activity,"OK",Toast.LENGTH_LONG).show();
    }
    // définir une méthode:
    private ArrayList<Livre> parsejsonFile(String jString) throws JSONException {
        ArrayList<Livre> listlivres=new ArrayList<Livre>();
        //on va créer l'objet json à partir de la chaine
        //on ajoute l'exception à la signature d'une methode si on pense
        //qu'on n'aura qu'un seul type d'exception:cela ajoute le "throws ...
        //sur la base de la chaine qu'on a récupéré
        JSONObject jsonObject=new JSONObject(jString);
        // =>  il faut mettre un try/catch à parsejsonFile(resultat);
        //recupere l'objet json livres:
        JSONObject livres=jsonObject.getJSONObject("livres");
        //on récupère l'array:
        JSONArray livre=livres.getJSONArray("livre");
        //on va parcourir l'array de JSON et recuperer les éléments:
        for (int i=0; i<livre.length();i++){
            //on récupère le 1er objet JSON:
            String id=livre.getJSONObject(i).getString("id");
            Log.d("JSONObject ",id);
            String nom=livre.getJSONObject(i).getString("title");
            Log.d("JSONObject ",nom);
            String auteur=livre.getJSONObject(i).getString("author");
            Log.d("JSONObject ",auteur);
            Livre livre1=new Livre(Integer.parseInt(id),nom,auteur);
            listlivres.add(livre1);
        }// fin de la boucle for
        return listlivres;
    }// fin de la métode: parsejsonFile
    // Methode pour la Metohde GET: qui va récupérer les données au format JSON
    // pour les convertir au format Livre et retourner un ArrayList
    private ArrayList<Livre> getAllLivres(String connection){
        //initialiser la connection à null
        HttpURLConnection httpURLConnection=null;
        //definir un objet
        String resultat=null;
        ArrayList<Livre> listLivres = null;
        try {
            URL url=new URL(connection);
            //initialiser la connexion
            //try {
            httpURLConnection= (HttpURLConnection) url.openConnection();
            //pour lire les données:
            InputStream inputStream= new BufferedInputStream(httpURLConnection.getInputStream());
            //boucle dans le strinbuilder: recupere json pour le traiter:
            int inChar;
            StringBuilder readStr=new StringBuilder();
            //boucle pour lire sur le flux de lecture char par char:
            while ((inChar=inputStream.read())!=-1){
                readStr.append((char)inChar);
            }
            //recuperer le resultat:
            resultat=readStr.toString();
            //on parse le resultat qui va convertir la chaine en json
            //et parser le JSON: cette methoe n'est pas encore créée:
            listLivres=parsejsonFile(resultat);
            //fermerla ressource:
            inputStream.close();
            httpURLConnection.disconnect();
            //} catch (MalformedURLException e) {
            // mieux le choix catch suivan{t
            //   e.printStackTrace();
            // on supprime ce catch car IOException est une erreur générique de 'Malfored...'
        } catch (IOException |JSONException e) {
            e.printStackTrace();
        }
        assert listLivres != null;
        for (Livre l:listLivres)  Log.d("JSONObjectListe",l.toString());
        return listLivres;
    } // fin de la methode getAllLivres
    // pour la Méthode PUT: la bonne pratique consisterait à remplacer String Livre
    // par Livre livre et à convertir l'Objet Livre en chaine String (format JSAON)
    private void insertLivre(String connection, String livre) {
        //initialiser la connection à null
        HttpURLConnection httpURLConnection=null;
        //definir un objet
        String resultat=null;
        int responseCode=0;
        try {
            URL url=new URL(connection);
            //initialiser la connexion
            //try {
            httpURLConnection= (HttpURLConnection) url.openConnection();
            // MODIFICATION pour POST:la méthode: (GET est par défaut)
            // ON spécifie le verbe http:POST
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Language","UTF-8");
            //on envoie les données à stocker dans la base:
            //metre les caractèrer d'echappement: ""; ensuite on colle le texte :
            //"{"id":"1","nom":"salle","auteur":"pierre"}";
            //alors les " sont comme des caractères:mais l'id ne sera pas inserré
            //String body="\"{\"id\":\"1\",\"nom\":\"salle\",\"auteur\":\"pierre\"}";
            //String body=livre;
            //on envoie les données dans le body:
            httpURLConnection.setDoOutput(true);
            //OutputStreamWriter est un pont entre les flux de caractères et les flux d'octet
            // on ouvre un flux d'écriture sur la connexion:
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
            //le write () à 1 param string: on écrit les données chaine (JSON)
            //sur le flux d'écriture
            outputStreamWriter.write(livre);
            //pour valider l'écriture: transmettre les données au restfullwebservice:
            outputStreamWriter.flush();
            //pour vérifier: on récupère le code de réponse du serveur es ok (200):
            responseCode=httpURLConnection.getResponseCode();
            resultat= String.valueOf(responseCode);
            //cela car resultat es un String.
            //on pourrait ouvrir un flux de lecture pour demander le code de retour du serveur
            //une requete GET
            outputStreamWriter.close();
            //pour tester le code 200 => onPostexecute
            httpURLConnection.disconnect();
            //} catch (MalformedURLException e) {
            // mieux le choix catch suivan{t
            //   e.printStackTrace();
            // on supprime ce catch car IOException est une erreur générique de 'Malfored...'
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //fin de insertLivre
    // pour la Méthode PUT: la bonne pratique consisterait à remplacer String Livre
    // par Livre livre et à convertir l'Objet Livre en chaine String (format JSAON)
    private void updateLivre(String connection, String livre){
        //initialiser la connection à null
        HttpURLConnection httpURLConnection=null;
        //definir un objet
        String resultat=null;
        int responseCode=0;
        try {
            URL url=new URL(connection);
            //initialiser la connexion
            //try {
            httpURLConnection= (HttpURLConnection) url.openConnection();
            // MODIFICATION pour POST:la méthode: (GET est par défaut)
            // ON spécifie le verbe http:POST
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Accept-Language","UTF-8");
            //on envoie les données à stocker dans la base:
            //metre les caractèrer d'echappement: ""; ensuite on colle le texte :
            //"{"id":"1","nom":"salle","auteur":"pierre"}";
            //alors les " sont comme des caractères:mais l'id ne sera pas inserré
            //String body="\"{\"id\":\"1\",\"nom\":\"salle\",\"auteur\":\"pierre\"}";
            //String body=livre;
            //on envoie les données dans le body:
            httpURLConnection.setDoOutput(true);
            //OutputStreamWriter est un pont entre les flux de caractères et les flux d'octet
            // on ouvre un flux d'écriture sur la connexion:
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
            //le write () à 1 param string: on écrit les données chaine (JSON)
            //sur le flux d'écriture
            outputStreamWriter.write(livre);
            //pour valider l'écriture: transmettre les données au restfullwebservice:
            outputStreamWriter.flush();
            //pour vérifier: on récupère le code de réponse du serveur es ok (200):
            responseCode=httpURLConnection.getResponseCode();
            resultat= String.valueOf(responseCode);
            //cela car resultat es un String.
            //on pourrait ouvrir un flux de lecture pour demander le code de retour du serveur
            //une requete GET
            outputStreamWriter.close();
            //pour tester le code 200 => onPostexecute
            httpURLConnection.disconnect();
            //} catch (MalformedURLException e) {
            // mieux le choix catch suivan{t
            //   e.printStackTrace();
            // on supprime ce catch car IOException est une erreur générique de 'Malfored...'
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //fin de deleteLivre
    // pour la Méthode DELETE: la bonne pratique consisterait à remplacer String Livre
    // par Livre livre et à convertir l'Objet Livre en chaine String (format JSAON)
    // on peut retourner un int qui correspondrait à responseCode pour
    // s'assurer que la méthode s'est bien exécutée
    private void deleteLivre(String connection, int id){
        //initialiser la connection à null
        HttpURLConnection httpURLConnection=null;
        //definir un objet
        String resultat=null;
        int responseCode=0;
        try {
            // connection se termine par /
            URL url=new URL(connection+id);
            //initialiser la connexion
            //try {
            httpURLConnection= (HttpURLConnection) url.openConnection();
            // MODIFICATION pour POST:la méthode: (GET est par défaut)
            // ON spécifie le verbe http:POST
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Accept-Language","UTF-8");
            //on envoie les données à stocker dans la base:
            //metre les caractèrer d'echappement: ""; ensuite on colle le texte :
            //"{"id":"1","nom":"salle","auteur":"pierre"}";
            //alors les " sont comme des caractères:mais l'id ne sera pas inserré
            //String body="\"{\"id\":\"1\",\"nom\":\"salle\",\"auteur\":\"pierre\"}";
            //on envoie les données dans le body:
            httpURLConnection.setDoOutput(true);
            //OutputStreamWriter est un pont entre les flux de caractères et les flux d'octet
            // on ouvre un flux d'écriture sur la connexion:
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(httpURLConnection.getOutputStream());
            //le write () à 1 param string: on écrit les données chaine (JSON)
            //sur le flux d'écriture
            //outputStreamWriter.write(body);
            //pour valider l'écriture: transmettre les données au restfullwebservice:
            outputStreamWriter.flush();
            //pour vérifier: on récupère le code de réponse du serveur es ok (200):
            responseCode=httpURLConnection.getResponseCode();
            resultat= String.valueOf(responseCode);
            //cela car resultat es un String.
            //on pourrait ouvrir un flux de lecture pour demander le code de retour du serveur
            //une requete GET
            outputStreamWriter.close();
            //pour tester le code 200 => onPostexecute
            httpURLConnection.disconnect();
            //} catch (MalformedURLException e) {
            // mieux le choix catch suivan{t
            //   e.printStackTrace();
            // on supprime ce catch car IOException est une erreur générique de 'Malfored...'
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //fin de deleteLivre
}// fin de la classe AsyncTask