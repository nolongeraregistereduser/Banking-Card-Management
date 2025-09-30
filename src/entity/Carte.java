package entity;

public  sealed class Carte permits CarteDebit, CarteCredit, CartePrepayee {

    private final int id;
    private final String numero;
    private final java.sql.Date dateExpiration;
    private final String status;
    private final int clientId;


    public Carte(int id, String numero, java.sql.Date dateExpiration, String status, int clientId) {
        this.id = id;
        this.numero = numero;
        this.dateExpiration = dateExpiration;
        this.status = status;
        this.clientId = clientId;
    }

}
