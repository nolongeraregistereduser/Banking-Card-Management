package service;

import dao.CarteDAO;
import entity.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;


public class CarteService implements BaseService<Carte, Integer> {
    private final CarteDAO carteDAO;

    // Status constants
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_BLOQUEE = "BLOQUEE";
    public static final String STATUS_SUSPENDUE = "SUSPENDUE";

    // Default values
    private static final BigDecimal DEFAULT_PLAFOND_JOURNALIER = BigDecimal.valueOf(1000.0);
    private static final BigDecimal DEFAULT_PLAFOND_MENSUEL = BigDecimal.valueOf(5000.0);
    private static final BigDecimal DEFAULT_TAUX_INTERET = BigDecimal.valueOf(15.0);
    private static final BigDecimal DEFAULT_SOLDE_PREPAYEE = BigDecimal.valueOf(0.0);

    public CarteService() {
        this.carteDAO = new CarteDAO();
    }

    // Business logic: Generate unique card number
    private String genererNumeroUnique() throws SQLException {
        String numero;
        Random random = new Random();
        do {
            numero = String.valueOf(1000000000000000L + (long)(random.nextDouble() * 8999999999999999L));
        } while (carteDAO.findByNumero(numero).isPresent());
        return numero;
    }

    // Business logic: Validate card data
    private void validerCarte(Carte carte) {
        if (carte == null) {
            throw new IllegalArgumentException("Carte cannot be null");
        }
        if (carte.getClientId() <= 0) {
            throw new IllegalArgumentException("Client ID must be positive");
        }
    }

    // Business logic: Set default values for card types
    private void setDefaultValues(Carte carte) {
        if (carte instanceof CarteDebit debit && debit.getPlafondJournalier() == null) {
            debit.setPlafondJournalier(DEFAULT_PLAFOND_JOURNALIER);
        } else if (carte instanceof CarteCredit credit) {
            if (credit.getPlafondMensuel() == null) {
                credit.setPlafondMensuel(DEFAULT_PLAFOND_MENSUEL);
            }
            if (credit.getTauxInteret() == null) {
                credit.setTauxInteret(DEFAULT_TAUX_INTERET);
            }
        } else if (carte instanceof CartePrepayee prepayee && prepayee.getSoldeDisponible() == null) {
            prepayee.setSoldeDisponible(DEFAULT_SOLDE_PREPAYEE);
        }
    }

    @Override
    public void add(Carte carte) throws SQLException {
        // Business validation
        validerCarte(carte);

        // Business logic: Generate unique number if not provided
        if (carte.getNumero() == null || carte.getNumero().isEmpty()) {
            carte.setNumero(genererNumeroUnique());
        }

        // Business logic: Set default status if not provided
        if (carte.getStatus() == null || carte.getStatus().isEmpty()) {
            carte.setStatus(STATUS_ACTIVE);
        }

        // Business logic: Set default expiration date if not provided
        if (carte.getDateExpiration() == null) {
            carte.setDateExpiration(Date.valueOf(LocalDate.now().plusYears(3)));
        }

        // Business logic: Set default values based on card type
        setDefaultValues(carte);

        // Save to database
        carteDAO.save(carte);
        System.out.println("Carte créée avec succès.");
    }

    @Override
    public Carte findById(Integer id) throws SQLException {
        // Business validation
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Card ID must be positive");
        }

        Optional<Carte> carte = carteDAO.findById(id);
        if (carte.isPresent()) {
            // Business logic: Apply default values if needed
            setDefaultValues(carte.get());
            return carte.get();
        }
        return null;
    }

    @Override
    public List<Carte> findAll() throws SQLException {
        List<Carte> cartes = carteDAO.findAll();
        // Business logic: Apply default values to all cards
        cartes.forEach(this::setDefaultValues);
        return cartes;
    }

    @Override
    public void update(Carte carte) throws SQLException {
        // Business validation
        validerCarte(carte);

        if (carte.getId() <= 0) {
            throw new IllegalArgumentException("Card ID must be positive");
        }

        // Business logic: Check if card exists
        if (carteDAO.findById(carte.getId()).isEmpty()) {
            throw new SQLException("Card not found with ID: " + carte.getId());
        }

        carteDAO.update(carte);
        System.out.println("Carte mise à jour avec succès.");
    }

    @Override
    public void remove(Integer id) throws SQLException {
        // Business validation
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Card ID must be positive");
        }

        // Business logic: Check if card exists before deleting
        if (carteDAO.findById(id).isEmpty()) {
            throw new SQLException("Card not found with ID: " + id);
        }

        carteDAO.delete(id);
        System.out.println("Carte supprimée avec succès.");
    }

    // Business-specific methods
    public List<Carte> findByClientId(Integer clientId) throws SQLException {
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("Client ID must be positive");
        }

        List<Carte> cartes = carteDAO.findByClientId(clientId);
        cartes.forEach(this::setDefaultValues);
        return cartes;
    }

    public void activerCarte(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Card ID must be positive");
        }

        Optional<Carte> carteOpt = carteDAO.findById(id);
        if (carteOpt.isEmpty()) {
            throw new SQLException("Card not found with ID: " + id);
        }

        Carte carte = carteOpt.get();
        // Business logic: Only allow activation from certain statuses
        if (STATUS_ACTIVE.equals(carte.getStatus())) {
            throw new SQLException("Card is already active");
        }

        carte.setStatus(STATUS_ACTIVE);
        carteDAO.update(carte);
        System.out.println("Carte activée avec succès.");
    }

    public void bloquerCarte(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Card ID must be positive");
        }

        Optional<Carte> carteOpt = carteDAO.findById(id);
        if (carteOpt.isEmpty()) {
            throw new SQLException("Card not found with ID: " + id);
        }

        Carte carte = carteOpt.get();
        carte.setStatus(STATUS_BLOQUEE);
        carteDAO.update(carte);
        System.out.println("Carte bloquée avec succès.");
    }

    public void suspendreCarte(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Card ID must be positive");
        }

        Optional<Carte> carteOpt = carteDAO.findById(id);
        if (carteOpt.isEmpty()) {
            throw new SQLException("Card not found with ID: " + id);
        }

        Carte carte = carteOpt.get();
        carte.setStatus(STATUS_SUSPENDUE);
        carteDAO.update(carte);
        System.out.println("Carte suspendue avec succès.");
    }

    public Optional<Carte> findByNumero(String numero) throws SQLException {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }

        Optional<Carte> carte = carteDAO.findByNumero(numero);
        if (carte.isPresent()) {
            setDefaultValues(carte.get());
        }
        return carte;
    }

    public List<Carte> findByStatut(String statut) throws SQLException {
        if (statut == null || statut.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        List<Carte> cartes = carteDAO.findByStatut(statut);
        cartes.forEach(this::setDefaultValues);
        return cartes;
    }

    // Business logic: Check if card can perform operations
    public boolean peutEffectuerOperation(Integer carteId) throws SQLException {
        Carte carte = findById(carteId);
        return carte != null && STATUS_ACTIVE.equals(carte.getStatus());
    }

    // Business logic: Renew card
    public void renouvelerCarte(Integer id) throws SQLException {
        Carte carte = findById(id);
        if (carte == null) {
            throw new SQLException("Card not found with ID: " + id);
        }

        carte.setDateExpiration(Date.valueOf(LocalDate.now().plusYears(3)));
        carte.setStatus(STATUS_ACTIVE);
        carteDAO.update(carte);
        System.out.println("Carte renouvelée avec succès.");
    }
}
