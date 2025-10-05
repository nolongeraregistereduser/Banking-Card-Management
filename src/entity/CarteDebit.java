package entity;

import java.math.BigDecimal;
import java.sql.Date;

public final class CarteDebit extends Carte {

    private BigDecimal plafondJournalier = BigDecimal.valueOf(1000.0);

    public CarteDebit(int id, String numero, Date dateExpiration, String status, int clientId, BigDecimal plafondJournalier) {
        super(id, numero, dateExpiration, status, clientId);
        this.plafondJournalier = plafondJournalier;
    }

    public BigDecimal getPlafondJournalier() {
        return plafondJournalier;
    }

    public void setPlafondJournalier(BigDecimal plafondJournalier) {
        this.plafondJournalier = plafondJournalier;
    }
}
