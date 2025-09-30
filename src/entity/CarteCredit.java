package entity;

public final class CarteCredit extends Carte {

    private double plafondMensuel = 5000.0;
    private double tauxInteretAnnuel = 15.0;

    public CarteCredit(int id, String numero, java.sql.Date dateExpiration, String status, int clientId, double plafondMensuel, double tauxInteretAnnuel) {
        super(id, numero, dateExpiration, status, clientId);
        this.plafondMensuel = plafondMensuel;
        this.plafondMensuel = tauxInteretAnnuel;
    }

}
