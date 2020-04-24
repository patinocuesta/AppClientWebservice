package com.example.appclientwebservice;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
public class SecondActivity extends AppCompatActivity {
    String action= null;
    Asynchrone as;
    EditText nom, auteur;
    Livre livre;
    Button b;
    //le nom et l'auteur, le livre:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        // récupérer le message l'intent, avec les données  l'action et le livre
        Intent intent=getIntent();
        action=intent.getStringExtra("action");//la clé: PUT, UPDATE, DELETE
        // c'est un objet , caster
        livre= (Livre) intent.getSerializableExtra("livre");
        nom= findViewById(R.id.nom);
        auteur=findViewById(R.id.auteur);
        //si le livre n'est pas null, on l'affiche dans le formulaire
        if (livre != null) {
            nom.setText(livre.getNom());
            auteur.setText(livre.getAuteur());
        }
        b=findViewById(R.id.envoyer);
        setB(action, b);
    }
    public void setB(String string, Button b){
        switch (string){
            case "PUT":
                b.setText("Mettre à jour");
                break;
            case "UPDATE":
                b.setText("Mettre à jour");
                break;
            case "DELETE":
                b.setText("Delete");
                break;
            case "POST":
                b.setText("Ajouter");
                break;
            case "GET":
                b.setText("Retourner");
                break;
            default:
                b.setText("Envoyer");
                break;
        }
    }
    public void envoi(View view) {
        //cette méthode doit vérifier l'action et en fonction de sa valeur
        // appeler la classe asynchrone pour créer , modifier, supprimer
        //des Données; et revenir à la MainActivity
        if (action.equals("POST")){
            String lnom=nom.getText().toString();
            String lauteur=auteur.getText().toString();
            //=> Asynchrone la chaine en dur
            String body="{\"id\":\"1\",\"title\":\""+lnom+"\",\"author\":\""+lauteur+"\"}";
            Log.d("valeur",body);
            //on instancie la classe Asynchrone
            as=new Asynchrone(this);//==> MainActivity
            String[] parametres={"http://10.0.2.2/webservice/webservice.php/","POST",body,"0"};
            as.execute(parametres);
            Intent intent=new Intent();
            // ce qu'il va transporter:
            intent.putExtra("result","GET");//clé, puis la valeur
            intent.putExtra("livre",body);
            // le 2ème stResult
            setResult(Activity.RESULT_OK,intent);// stop ce jour (?)
            finish();
        }
        if (action.equals("PUT")) {
            String lnom = nom.getText().toString();
            String lauteur = auteur.getText().toString();
            String body = "{\"id\":\"" +livre.getId() + "\",\"title\":\"" + lnom + "\",\"author\":\"" + lauteur + "\"}";
            Asynchrone as = new Asynchrone(this);
            String[] s = {"http://10.0.2.2/webservice/webservice.php/", "PUT", body,"0"};
            as.execute(s);
            Intent intent = new Intent();
            intent.putExtra("result", "GET");
            intent.putExtra("livre", body);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        if (action.equals("DELETE")) {
            String lnom = nom.getText().toString();
            String lauteur = auteur.getText().toString();
            Asynchrone as = new Asynchrone(this);
            String[] s = {"http://10.0.2.2/webservice/webservice.php/", "DELETE", null,String.valueOf(livre.getId())};
            as.execute(s);
            Intent intent = new Intent();
            intent.putExtra("result", "get");
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        if (action.equals("GET")) {
            finish();
        }
    }
    public void cancel(View view) {
        finish();
    }
}
