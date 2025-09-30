package entity;

public final class CartePrepayee extends Carte{

    private double soldeDisponible = 0.0;

    public CartePrepayee(int id, String numero, java.sql.Date dateExpiration, String status, int clientId, double soldeDisponible) {
        super(id, numero, dateExpiration, status, clientId);
        this.soldeDisponible = soldeDisponible;
    }
}
