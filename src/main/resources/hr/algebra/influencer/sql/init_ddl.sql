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

INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'admin', 'admin', 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'admin');
INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'brend', 'brend', 'BREND' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'brend');
INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'influencer', 'influencer', 'INFLUENCER' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'influencer');

INSERT INTO Platforma (Naziv) SELECT 'Instagram' WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'Instagram');
INSERT INTO Platforma (Naziv) SELECT 'TikTok'    WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'TikTok');
INSERT INTO Platforma (Naziv) SELECT 'YouTube'   WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'YouTube');
INSERT INTO Platforma (Naziv) SELECT 'Twitch'    WHERE NOT EXISTS (SELECT 1 FROM Platforma WHERE Naziv = 'Twitch');
