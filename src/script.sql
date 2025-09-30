-- Clear all tables and reset sequences
TRUNCATE TABLE AlerteFraude, OperationCarte, Carte, Client RESTART IDENTITY CASCADE;

CREATE TABLE Client (
                        id SERIAL PRIMARY KEY,
                        nom VARCHAR(100),
                        email VARCHAR(100),
                        telephone VARCHAR(20)
);

CREATE TABLE Carte (
                       id SERIAL PRIMARY KEY,
                       numero VARCHAR(20) UNIQUE,
                       dateExpiration DATE,
                       statut VARCHAR(20),
                       typeCarte VARCHAR(20),
                       idClient INT,
                       FOREIGN KEY (idClient) REFERENCES Client(id)
);

CREATE TABLE OperationCarte (
                                id SERIAL PRIMARY KEY,
                                date TIMESTAMP,
                                montant DECIMAL(10,2),
                                type VARCHAR(20),
                                lieu VARCHAR(100),
                                idCarte INT,
                                FOREIGN KEY (idCarte) REFERENCES Carte(id)
);

CREATE TABLE AlerteFraude (
                              id SERIAL PRIMARY KEY,
                              description VARCHAR(255),
                              niveau VARCHAR(20),
                              idCarte INT,
                              FOREIGN KEY (idCarte) REFERENCES Carte(id)
);

-- Insert 10 clients
INSERT INTO Client (nom, email, telephone) VALUES
('Alice Martin', 'alice.martin@email.com', '0601010101'),
('Bob Dupont', 'bob.dupont@email.com', '0602020202'),
('Carla Moreau', 'carla.moreau@email.com', '0603030303'),
('David Leroy', 'david.leroy@email.com', '0604040404'),
('Emma Petit', 'emma.petit@email.com', '0605050505'),
('Fabrice Noel', 'fabrice.noel@email.com', '0606060606'),
('Gina Rousseau', 'gina.rousseau@email.com', '0607070707'),
('Hugo Bernard', 'hugo.bernard@email.com', '0608080808'),
('Ines Dubois', 'ines.dubois@email.com', '0609090909'),
('Julien Faure', 'julien.faure@email.com', '0610101010');

-- Insert 10 cards (each linked to a client) with explicit id values
INSERT INTO Carte (id, numero, dateExpiration, statut, typeCarte, idClient) VALUES
(1, '400000000001', '2027-09-30', 'ACTIVE', 'DEBIT', 1),
(2, '400000000002', '2026-12-31', 'ACTIVE', 'CREDIT', 2),
(3, '400000000003', '2028-03-15', 'SUSPENDUE', 'PREPAYEE', 3),
(4, '400000000004', '2027-06-20', 'ACTIVE', 'DEBIT', 4),
(5, '400000000005', '2026-11-11', 'BLOQUEE', 'CREDIT', 5),
(6, '400000000006', '2028-01-01', 'ACTIVE', 'PREPAYEE', 6),
(7, '400000000007', '2027-08-08', 'ACTIVE', 'DEBIT', 7),
(8, '400000000008', '2026-10-10', 'SUSPENDUE', 'CREDIT', 8),
(9, '400000000009', '2028-05-05', 'ACTIVE', 'PREPAYEE', 9),
(10, '400000000010', '2027-04-04', 'ACTIVE', 'DEBIT', 10);

-- Insert 10 operations (each linked to a card)
INSERT INTO OperationCarte (date, montant, type, lieu, idCarte) VALUES
('2025-09-30 10:00:00', 120.50, 'ACHAT', 'Paris', 1),
('2025-09-30 11:00:00', 50.00, 'RETRAIT', 'Lyon', 2),
('2025-09-30 12:00:00', 200.00, 'PAIEMENTENLIGNE', 'Marseille', 3),
('2025-09-30 13:00:00', 75.25, 'ACHAT', 'Toulouse', 4),
('2025-09-30 14:00:00', 300.00, 'RETRAIT', 'Nice', 5),
('2025-09-30 15:00:00', 60.00, 'PAIEMENTENLIGNE', 'Bordeaux', 6),
('2025-09-30 16:00:00', 90.00, 'ACHAT', 'Lille', 7),
('2025-09-30 17:00:00', 150.00, 'RETRAIT', 'Nantes', 8),
('2025-09-30 18:00:00', 80.00, 'PAIEMENTENLIGNE', 'Strasbourg', 9),
('2025-09-30 19:00:00', 110.00, 'ACHAT', 'Grenoble', 10);

-- Insert 10 alerts (each linked to a card)
INSERT INTO AlerteFraude (description, niveau, idCarte) VALUES
('Montant élevé détecté', 'CRITIQUE', 1),
('Opérations rapprochées dans des lieux différents', 'AVERTISSEMENT', 2),
('Dépassement de plafond', 'INFO', 3),
('Achat suspect', 'CRITIQUE', 4),
('Retrait inhabituel', 'AVERTISSEMENT', 5),
('Paiement en ligne suspect', 'INFO', 6),
('Carte utilisée dans deux pays', 'CRITIQUE', 7),
('Tentative de retrait refusée', 'AVERTISSEMENT', 8),
('Solde insuffisant', 'INFO', 9),
('Carte bloquée pour suspicion de fraude', 'CRITIQUE', 10);
