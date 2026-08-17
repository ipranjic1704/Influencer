CREATE TABLE IF NOT EXISTS Korisnik (
    IDKorisnik   INT AUTO_INCREMENT PRIMARY KEY,
    UserName     VARCHAR(100) NOT NULL UNIQUE,
    Lozinka      VARCHAR(255) NOT NULL,
    Uloga        VARCHAR(20) NOT NULL,
    InfluencerID INT NULL
);

INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'admin', 'admin', 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'admin');
INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'brend', 'brend', 'BREND' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'brend');
INSERT INTO Korisnik (UserName, Lozinka, Uloga) SELECT 'influencer', 'influencer', 'INFLUENCER' WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'influencer');
