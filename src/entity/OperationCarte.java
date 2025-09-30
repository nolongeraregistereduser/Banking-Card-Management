package entity;

public record OperationCarte(int id, java.sql.Timestamp date, double montant, String type, String lieu, int idCarte) {}

