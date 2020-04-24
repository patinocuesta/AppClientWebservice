package com.example.appclientwebservice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
//  OnItemClickListener est un Interface permettant à la MainActivity d'écouter des évènements
//comme la sélection d'une ligne et de faire un traitement
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    // variable permettant de router le traitement en fonction de l'action
    String action=null;
    // le livreMaj  parmètre c'est le livre sélectionné dans la liste, envoyé
    // à l'autre activity
    Livre livreMaj=null;
    ListView listView; // on le récupère pour lui appliquer le listener
    // pour pouvoir récupérer la liste de livres obtenue dans doInBackGround
    // public et statique: pas en java, c'est possible en Android => acces
    //à partir d'une autre classe
    // static permet de récupérer la propriété avec l'écriture MainActivity.livres :
    public static ArrayList<Livre> livres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //récupérer la listeview
        listView= findViewById(R.id.liste);
        // permet de déclencher à la sélection d'une ligne :
        listView.setOnItemClickListener(this);
        // on récupère la liste des livres:
        // après avoir fait dans la classe Asynchrone, on a mis un Constructor
        // par Generate => Constructor, car on a séparé la classe Asynchrone
        // dans une autre Activity
        //(on a modifié le Constructor à la classe Asychrone, on lui passe un paramètre de type
        //Activity
        Asynchrone as=new Asynchrone(this);
        String[] parametres={"http://10.0.2.2/webservice/webservice.php/","GET",null,"0"};
        as.execute(parametres);
    }
    //Méthode du cycle de vie d'une Activity pour créer un Menu:
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // les infalters sont des classes qui permettent de convertir des fichiers XML
        //en Objet JAVA, comme setContentView dans MainActivity.java
       MenuInflater inflater=getMenuInflater();
        //l'infalter va faire la conversion:
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
        // pourquoi pas un boolean ?
        // => créer directement les details du Menu: void creation (menu item) ci-dessous:
        //de moi:
    }
    public void creation(MenuItem item) {
        action="POST";
        // origine cette MainActivity; dest: la SecondeActivity;
        Intent intent=new Intent(this,SecondActivity.class);
        //ce qu'il va transporter:
        intent.putExtra("action",action);// (clé, puis la valeur)
        //Lorsqu'on va clicker sur le bouton envoyer de SecondeActivity,
        //  choses vont se passer: 1: appel à la class Asynchrone pour M-à-j la BDD.
        //On doit revenir sur la MainActivity qui devra mettre à jour la liste en
        //appelant la class Asynchrone à son tour.
        startActivityForResult(intent, 1);
        // au sujet du requestCode, on peut avoir plusieurs intents:
        // il définit l'intent qui permet de savoir le traitement à faire après cet intent
    }// fin de creation
    // lorsqu'on va sélectionner un Item de la listView, en clickant sur une case à cocher
    //on sélectionne un livre que l'on veut modifier ou supprimer; et on enverra ce livre vers
    //la SecondeAcrivity pour qu'il soit modifié, idem pour delete.
    public void update(MenuItem item) {
        if (livreMaj != null){
            action="PUT";
            Intent intent=new Intent(this,SecondActivity.class);
            //ce qu'il va transporter:
            intent.putExtra("action",action);//clé, puis la valeur
            intent.putExtra("livre",livreMaj);
            //Lorsqu'on va clicker sur le bouton envoyer de SecondeActivity,
            //  choses vont se passer: 1: appel à la class Asynchrone pour M-à-j la BDD.
            //On doit revenir sur la MainActivity qui devra mettre à jour la liste en
            //appelant la class Asynchrone à son tour.
            startActivityForResult(intent, 1);
            // au sujet du requestCode, on peut avoir plusieurs intents:
            // il définit l'intent qui permet de savoir le traitement à faire après cet intent
        }//fin du if
    }
    public void read(MenuItem item) {
        if (livreMaj != null) {
            action = "GET";
            Intent intent = new Intent(this, SecondActivity.class);
            //ce qu'il va transporter:
            intent.putExtra("action", action);//clé, puis la valeur
            intent.putExtra("livre", livreMaj);
            startActivityForResult(intent, 1);
        }
    }
    public void delete(MenuItem item) {
        if (livreMaj != null){
            action="DELETE";
            Intent intent=new Intent(this,SecondActivity.class);
            //ce qu'il va transporter:
            intent.putExtra("action",action);//clé, puis la valeur
            intent.putExtra("livre",livreMaj);
            //Lorsqu'on va clicker sur le bouton envoyer de SecondeActivity,
            //  choses vont se passer: 1: appel à la class Asynchrone pour M-à-j la BDD.
            //On doit revenir sur la MainActivity qui devra mettre à jour la liste en
            //appelant la class Asynchrone à son tour.
            startActivityForResult(intent, 1);
            // au sujet du requestCode, on peut avoir plusieurs intents:
            // il définit l'intent qui permet de savoir le traitement à faire après cet intent
        }//fin du if
    }
    // cette méthode sera appelée au retour de l'activity SecondeActivity vers MainACtivity
    //à cause de startActivityForResult
    //elle appellera la classe Asynchrone pour récupérer la liste des livres
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String[] s={"http://10.0.2.2/webservice/webservice.php/","GET", null,"0"};
        // on instancie Asynchrone
        Asynchrone as= new Asynchrone(this);
        as.execute(s);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // on doir récupérer le livre correspondant à la sélection:
        // position représente l'indice dans l'ArrayListe de Livre
           livreMaj=MainActivity.livres.get(position);// c'est l'id dans l'Array
    }
} // fin de MainActivity