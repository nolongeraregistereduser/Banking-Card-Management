package entity;

import java.sql.Date;

public final class CarteDebit extends Carte {


    private double plafondJournalier = 1000.0;

    public CarteDebit(int id, String numero, Date dateExpiration, String status, int clientId, double plafondJournalier) {
        super(id, numero, dateExpiration, status, clientId);
        this.plafondJournalier = plafondJournalier;
    }
}
