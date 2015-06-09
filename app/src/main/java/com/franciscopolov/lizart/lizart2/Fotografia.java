package com.franciscopolov.lizart.lizart2;

public class Fotografia {
    private Integer id;
    private Integer id_usuario;
    private String titulo;
    private String ISO;
    private String vel_obturacion;
    private String apertura;
    private String fecha;
    private String url;

    public Fotografia() {
        this.id=null;
        this.titulo="";
        this.ISO="";
        this.vel_obturacion="";
        this.apertura="";
        this.fecha="";
        this.url="";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getISO() {
        return ISO;
    }

    public void setISO(String ISO) {
        this.ISO = ISO;
    }

    public String getVel_obturacion() {
        return vel_obturacion;
    }

    public void setVel_obturacion(String vel_obturacion) {
        this.vel_obturacion = vel_obturacion;
    }

    public String getApertura() {
        return apertura;
    }

    public void setApertura(String apertura) {
        this.apertura = apertura;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
