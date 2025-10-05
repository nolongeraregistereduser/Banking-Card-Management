package entity;

import java.math.BigDecimal;

public final class CartePrepayee extends Carte{

    private BigDecimal soldeDisponible = BigDecimal.valueOf(0.0);

    public CartePrepayee(int id, String numero, java.sql.Date dateExpiration, String status, int clientId, BigDecimal soldeDisponible) {
        super(id, numero, dateExpiration, status, clientId);
        this.soldeDisponible = soldeDisponible;
    }

    public BigDecimal getSoldeDisponible() {
        return soldeDisponible;
    }

    public void setSoldeDisponible(BigDecimal soldeDisponible) {
        this.soldeDisponible = soldeDisponible;
    }
}
