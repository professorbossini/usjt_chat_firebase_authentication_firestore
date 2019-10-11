package br.com.bossini.usjt_chat_firebase_authentication_firestore;

import java.util.Date;

class Mensagem implements Comparable <Mensagem>{

    private Date data;
    private String texto;
    private String usuario;

    public Mensagem (String usuario, Date data, String texto){
        setUsuario(usuario);
        setData(data);
        setTexto(texto);
    }


    public Mensagem (){

    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public int compareTo(Mensagem mensagem) {
        return this.data.compareTo(mensagem.data);
    }
}
