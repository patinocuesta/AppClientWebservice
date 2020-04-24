package com.example.appclientwebservice;

import java.io.Serializable;
//Va servir à récupérer les Données au format JSON pour les stocker dans
//un ArrayList<Livre>, pour créer un Adapter pour une ListVeiw
//Comme on va créer une Activity pour inserrer des Données,
// on devra communiquer un Objet Livre via un Intent à une autre Activity:
//Il y a un Objet qui permet de sérialiser sous Android de manière optimale
// c'est Parcelable
public class Livre implements Serializable {
    private int id;
    private String nom;
    private String auteur;
    public Livre(int id, String nom, String auteur) {
        this.id = id;
        this.nom = nom;
        this.auteur = auteur;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getAuteur() {
        return auteur;
    }
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    @Override
    public String toString() {
        return "Titre: "+this.nom + "\nAuteur: " + this.auteur;
    }

}