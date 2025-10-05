package entity;

public  sealed class Carte permits CarteDebit, CarteCredit, CartePrepayee {

    private  int id;
    private  String numero;
    private  java.sql.Date dateExpiration;
    private String status;
    private  int clientId;


    public Carte(int id, String numero, java.sql.Date dateExpiration, String status, int clientId) {
        this.id = id;
        this.numero = numero;
        this.dateExpiration = dateExpiration;
        this.status = status;
        this.clientId = clientId;
    }


    public int getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public java.sql.Date getDateExpiration() {
        return dateExpiration;
    }

    public String getStatus() {
        return status;
    }

    public int getClientId() {
        return clientId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateExpiration(java.sql.Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
}
