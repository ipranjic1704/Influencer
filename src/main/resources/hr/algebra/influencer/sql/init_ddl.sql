-- Grad mora postojati prije Influencer tablice jer Influencer.GradID na njega referencira.
CREATE TABLE IF NOT EXISTS Grad (
    IDGrad INT AUTO_INCREMENT PRIMARY KEY,
    Naziv  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Influencer (
    IDInfluencer   INT AUTO_INCREMENT PRIMARY KEY,
    ImeNadimak     VARCHAR(255) NOT NULL,
    BrojPratitelja INT NOT NULL DEFAULT 0,
    EngagementRate DOUBLE NOT NULL DEFAULT 0.0,
    Zemlja         VARCHAR(100),
    GradID         INT REFERENCES Grad(IDGrad) ON DELETE SET NULL,
    JezikSadrzaja  VARCHAR(100),
    ProfilnaSlika  VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS Platforma (
    IDPlatforma  INT AUTO_INCREMENT PRIMARY KEY,
    Naziv        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS InfluencerPlatforma (
    IDInfluencer INT NOT NULL REFERENCES Influencer(IDInfluencer) ON DELETE CASCADE,
    IDPlatforma  INT NOT NULL REFERENCES Platforma(IDPlatforma)   ON DELETE CASCADE,
    PRIMARY KEY (IDInfluencer, IDPlatforma)
);

CREATE TABLE IF NOT EXISTS Korisnik (
    IDKorisnik   INT AUTO_INCREMENT PRIMARY KEY,
    UserName     VARCHAR(100) NOT NULL UNIQUE,
    Lozinka      VARCHAR(255) NOT NULL,
    Uloga        VARCHAR(20) NOT NULL,
    InfluencerID INT NULL REFERENCES Influencer(IDInfluencer) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS Nisa (
    IDNisa  INT AUTO_INCREMENT PRIMARY KEY,
    Naziv   VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS InfluencerNisa (
    IDInfluencer INT NOT NULL REFERENCES Influencer(IDInfluencer) ON DELETE CASCADE,
    IDNisa       INT NOT NULL REFERENCES Nisa(IDNisa)               ON DELETE CASCADE,
    PRIMARY KEY (IDInfluencer, IDNisa)
);

CREATE TABLE IF NOT EXISTS TipSadrzaja (
    IDTipSadrzaja INT AUTO_INCREMENT PRIMARY KEY,
    Naziv         VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS InfluencerTipSadrzaja (
    IDInfluencer  INT NOT NULL REFERENCES Influencer(IDInfluencer)   ON DELETE CASCADE,
    IDTipSadrzaja INT NOT NULL REFERENCES TipSadrzaja(IDTipSadrzaja) ON DELETE CASCADE,
    PRIMARY KEY (IDInfluencer, IDTipSadrzaja)
);

-- Brand mora postojati prije BrandSuradnja tablice jer BrandSuradnja.BrandID na njega referencira.
CREATE TABLE IF NOT EXISTS Brand (
    IDBrand INT AUTO_INCREMENT PRIMARY KEY,
    Naziv   VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS BrandSuradnja (
    IDBrandSuradnja INT AUTO_INCREMENT PRIMARY KEY,
    NazivKampanje   VARCHAR(255) NOT NULL,
    BrandID         INT NOT NULL REFERENCES Brand(IDBrand),
    Godina          INT NOT NULL,
    Status          VARCHAR(20) NOT NULL
);

-- "Tim" brand suradnje - influenceri dodani u kampanju drag & dropom.
CREATE TABLE IF NOT EXISTS BrandSuradnjaInfluencer (
    IDBrandSuradnja INT NOT NULL REFERENCES BrandSuradnja(IDBrandSuradnja) ON DELETE CASCADE,
    IDInfluencer    INT NOT NULL REFERENCES Influencer(IDInfluencer)       ON DELETE CASCADE,
    PRIMARY KEY (IDBrandSuradnja, IDInfluencer)
);

-- Prava baza-side procedura: H2 CREATE ALIAS registrira Java metodu pod SQL imenom.
-- Connection parametar H2 automatski puni trenutnom vezom, pa ne ulazi u SQL potpis (KREIRAJ_ADMINA()).
CREATE ALIAS IF NOT EXISTS KREIRAJ_ADMINA FOR "hr.algebra.influencer.BazaPodataka.kreirajAdminAkoNePostoji";
