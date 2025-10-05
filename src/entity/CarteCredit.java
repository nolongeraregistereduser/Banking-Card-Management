package entity;

import java.math.BigDecimal;

public final class CarteCredit extends Carte {

    private BigDecimal plafondMensuel = BigDecimal.valueOf(5000.0);
    private BigDecimal tauxInteret = BigDecimal.valueOf(15.0);

    public CarteCredit(int id, String numero, java.sql.Date dateExpiration, String status, int clientId, BigDecimal plafondMensuel, BigDecimal tauxInteret) {
        super(id, numero, dateExpiration, status, clientId);
        this.plafondMensuel = plafondMensuel;
        this.tauxInteret = tauxInteret;
    }

    public BigDecimal getPlafondMensuel() {
        return plafondMensuel;
    }

    public void setPlafondMensuel(BigDecimal plafondMensuel) {
        this.plafondMensuel = plafondMensuel;
    }

    public BigDecimal getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(BigDecimal tauxInteret) {
        this.tauxInteret = tauxInteret;
    }
}
