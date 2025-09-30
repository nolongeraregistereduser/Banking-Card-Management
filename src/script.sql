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
