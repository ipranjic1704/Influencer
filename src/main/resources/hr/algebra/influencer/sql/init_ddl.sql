CREATE TABLE IF NOT EXISTS Influencer (
    IDInfluencer   INT AUTO_INCREMENT PRIMARY KEY,
    ImeNadimak     VARCHAR(255) NOT NULL,
    BrojPratitelja INT NOT NULL DEFAULT 0,
    EngagementRate DOUBLE NOT NULL DEFAULT 0.0,
    Zemlja         VARCHAR(100),
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

CREATE TABLE IF NOT EXISTS BrandSuradnja (
    IDBrandSuradnja INT AUTO_INCREMENT PRIMARY KEY,
    NazivKampanje   VARCHAR(255) NOT NULL,
    NazivBrenda     VARCHAR(255) NOT NULL,
    Godina          INT NOT NULL,
    Status          VARCHAR(20) NOT NULL
);

-- "Tim" brand suradnje - influenceri dodani u kampanju drag & dropom.
CREATE TABLE IF NOT EXISTS BrandSuradnjaInfluencer (
    IDBrandSuradnja INT NOT NULL REFERENCES BrandSuradnja(IDBrandSuradnja) ON DELETE CASCADE,
    IDInfluencer    INT NOT NULL REFERENCES Influencer(IDInfluencer)       ON DELETE CASCADE,
    PRIMARY KEY (IDBrandSuradnja, IDInfluencer)
);

INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'admin', 'admin', 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'admin');
INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'brend', 'brend', 'BREND' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'brend');
INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'influencer', 'influencer', 'INFLUENCER' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'influencer');

INSERT INTO Platforma (Naziv) SELECT 'Instagram' WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'Instagram');
INSERT INTO Platforma (Naziv) SELECT 'TikTok'    WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'TikTok');
INSERT INTO Platforma (Naziv) SELECT 'YouTube'   WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'YouTube');
INSERT INTO Platforma (Naziv) SELECT 'Twitch'    WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'Twitch');

INSERT INTO Nisa (Naziv) SELECT 'Beauty'    WHERE NOT EXISTS (SELECT 1 FROM Nisa WHERE Naziv = 'Beauty');
INSERT INTO Nisa (Naziv) SELECT 'Fitness'   WHERE NOT EXISTS (SELECT 1 FROM Nisa WHERE Naziv = 'Fitness');
INSERT INTO Nisa (Naziv) SELECT 'Tech'      WHERE NOT EXISTS (SELECT 1 FROM Nisa WHERE Naziv = 'Tech');
INSERT INTO Nisa (Naziv) SELECT 'Lifestyle' WHERE NOT EXISTS (SELECT 1 FROM Nisa WHERE Naziv = 'Lifestyle');
INSERT INTO Nisa (Naziv) SELECT 'Gaming'    WHERE NOT EXISTS (SELECT 1 FROM Nisa WHERE Naziv = 'Gaming');

INSERT INTO TipSadrzaja (Naziv) SELECT 'Reels'      WHERE NOT EXISTS (SELECT 1 FROM TipSadrzaja WHERE Naziv = 'Reels');
INSERT INTO TipSadrzaja (Naziv) SELECT 'Shorts'     WHERE NOT EXISTS (SELECT 1 FROM TipSadrzaja WHERE Naziv = 'Shorts');
INSERT INTO TipSadrzaja (Naziv) SELECT 'Recenzije'  WHERE NOT EXISTS (SELECT 1 FROM TipSadrzaja WHERE Naziv = 'Recenzije');
INSERT INTO TipSadrzaja (Naziv) SELECT 'Tutoriali'  WHERE NOT EXISTS (SELECT 1 FROM TipSadrzaja WHERE Naziv = 'Tutoriali');
