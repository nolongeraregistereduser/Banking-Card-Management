package service;

import dao.CarteDAO;
import entity.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CarteService {
    private final CarteDAO carteDAO;

    public CarteService() {
        this.carteDAO = new CarteDAO();
    }

    public void creerCarte(Carte carte) throws SQLException {
        // Business logic can be added here (e.g., unique number generation)
        carteDAO.save(carte);
    }

    public Optional<Carte> trouverCarteParId(Long id) throws SQLException {
        return carteDAO.findById(id);
    }

    public List<Carte> trouverCartesParClient(Long clientId) throws SQLException {
        return carteDAO.findByClientId(clientId);
    }

    public List<Carte> listerToutesLesCartes() throws SQLException {
        return carteDAO.findAll();
    }

    public void activerCarte(Long id) throws SQLException {
        Optional<Carte> carteOpt = carteDAO.findById(id);
        if (carteOpt.isPresent()) {
            Carte carte = carteOpt.get();
            carte.setStatut(StatutCarte.ACTIVE);
            carteDAO.update(carte);
        }
    }

    public void bloquerCarte(Long id) throws SQLException {
        Optional<Carte> carteOpt = carteDAO.findById(id);
        if (carteOpt.isPresent()) {
            Carte carte = carteOpt.get();
            carte.setStatut(StatutCarte.BLOQUEE);
            carteDAO.update(carte);
        }
    }

    public void suspendreCarte(Long id) throws SQLException {
        Optional<Carte> carteOpt = carteDAO.findById(id);
        if (carteOpt.isPresent()) {
            Carte carte = carteOpt.get();
            carte.setStatut(StatutCarte.SUSPENDUE);
            carteDAO.update(carte);
        }
    }

    public void supprimerCarte(Long id) throws SQLException {
        carteDAO.delete(id);
    }

    public Optional<Carte> trouverCarteParNumero(String numero) throws SQLException {
        return carteDAO.findByNumero(numero);
    }

    public List<Carte> trouverCartesParStatut(StatutCarte statut) throws SQLException {
        return carteDAO.findByStatut(statut);
    }

    // TODO: Add methods for plafond verification and renewal
}

