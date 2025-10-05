-- Clear all tables and reset sequences for PostgreSQL
DROP TABLE IF EXISTS alertefraude CASCADE;
DROP TABLE IF EXISTS operationcarte CASCADE;
DROP TABLE IF EXISTS carte CASCADE;
DROP TABLE IF EXISTS client CASCADE;

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    email VARCHAR(100),
    telephone VARCHAR(20),
    password VARCHAR(255) NOT NULL
);

CREATE TABLE carte (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(20) UNIQUE,
    dateexpiration DATE,
    statut VARCHAR(20),
    typecarte VARCHAR(20),
    plafondjournalier DECIMAL(10,2),
    plafondmensuel DECIMAL(10,2),
    tauxinteret DECIMAL(5,2),
    soldedisponible DECIMAL(10,2),
    idclient INT,
    FOREIGN KEY (idclient) REFERENCES client(id)
);

CREATE TABLE operationcarte (
    id SERIAL PRIMARY KEY,
    date TIMESTAMP,
    montant DECIMAL(10,2),
    type VARCHAR(20),
    lieu VARCHAR(100),
    idcarte INT,
    FOREIGN KEY (idcarte) REFERENCES carte(id)
);

CREATE TABLE alertefraude (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255),
    niveau VARCHAR(20),
    idcarte INT,
    FOREIGN KEY (idcarte) REFERENCES carte(id)
);

-- Insert 10 clients with password '123456'
INSERT INTO client (nom, email, telephone, password) VALUES
('Alice Martin', 'alice.martin@email.com', '0601010101', '123456'),
('Bob Dupont', 'bob.dupont@email.com', '0602020202', '123456'),
('Carla Moreau', 'carla.moreau@email.com', '0603030303', '123456'),
('David Leroy', 'david.leroy@email.com', '0604040404', '123456'),
('Emma Petit', 'emma.petit@email.com', '0605050505', '123456'),
('Fabrice Noel', 'fabrice.noel@email.com', '0606060606', '123456'),
('Gina Rousseau', 'gina.rousseau@email.com', '0607070707', '123456'),
('Hugo Bernard', 'hugo.bernard@email.com', '0608080808', '123456'),
('Ines Dubois', 'ines.dubois@email.com', '0609090909', '123456'),
('Julien Faure', 'julien.faure@email.com', '0610101010', '123456');

-- Insert 10 cards with proper type-specific data
INSERT INTO carte (numero, dateexpiration, statut, typecarte, plafondjournalier, plafondmensuel, tauxinteret, soldedisponible, idclient) VALUES
('400000000001', '2027-09-30', 'ACTIVE', 'CarteDebit', 1000.00, NULL, NULL, NULL, 1),
('400000000002', '2026-12-31', 'ACTIVE', 'CarteCredit', NULL, 5000.00, 15.00, NULL, 2),
('400000000003', '2028-03-15', 'SUSPENDUE', 'CartePrepayee', NULL, NULL, NULL, 250.00, 3),
('400000000004', '2027-06-20', 'ACTIVE', 'CarteDebit', 800.00, NULL, NULL, NULL, 4),
('400000000005', '2026-11-11', 'BLOQUEE', 'CarteCredit', NULL, 3000.00, 18.50, NULL, 5),
('400000000006', '2028-01-01', 'ACTIVE', 'CartePrepayee', NULL, NULL, NULL, 500.00, 6),
('400000000007', '2027-08-08', 'ACTIVE', 'CarteDebit', 1200.00, NULL, NULL, NULL, 7),
('400000000008', '2026-10-10', 'SUSPENDUE', 'CarteCredit', NULL, 7000.00, 12.00, NULL, 8),
('400000000009', '2028-05-05', 'ACTIVE', 'CartePrepayee', NULL, NULL, NULL, 150.00, 9),
('400000000010', '2027-04-04', 'ACTIVE', 'CarteDebit', 900.00, NULL, NULL, NULL, 10);

-- Insert 10 operations (each linked to a card)
INSERT INTO operationcarte (date, montant, type, lieu, idcarte) VALUES
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
INSERT INTO alertefraude (description, niveau, idcarte) VALUES
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
