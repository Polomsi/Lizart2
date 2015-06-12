package com.franciscopolov.lizart.lizart2;

/**
 * Created by Polo on 11/06/2015.
 */
public class Comentario {
    Integer id_fotografia;
    Integer id_usuario;
    String fecha;
    String coment;
    String nom_usu;
    String url_fotoUsu;

    public Integer getId_fotografia() {
        return id_fotografia;
    }

    public void setId_fotografia(Integer id_fotografia) {
        this.id_fotografia = id_fotografia;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getComent() {
        return coment;
    }

    public void setComent(String coment) {
        this.coment = coment;
    }

    public String getNom_usu() {
        return nom_usu;
    }

    public void setNom_usu(String nom_usu) {
        this.nom_usu = nom_usu;
    }

    public String getUrl_fotoUsu() {
        return url_fotoUsu;
    }

    public void setUrl_fotoUsu(String url_fotoUsu) {
        this.url_fotoUsu = url_fotoUsu;
    }
}
