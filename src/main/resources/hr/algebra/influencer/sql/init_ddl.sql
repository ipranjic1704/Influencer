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

INSERT INTO Brand (Naziv) SELECT 'Nike'    WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE Naziv = 'Nike');
INSERT INTO Brand (Naziv) SELECT 'Adidas'  WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE Naziv = 'Adidas');
INSERT INTO Brand (Naziv) SELECT 'Coca-Cola' WHERE NOT EXISTS (SELECT 1 FROM Brand WHERE Naziv = 'Coca-Cola');

-- Gradovi seedani izravno (ne cekamo admin uvoz s API-ja) da default influenceri odmah imaju grad.
INSERT INTO Grad (Naziv) SELECT 'Zagreb' WHERE NOT EXISTS (SELECT 1 FROM Grad WHERE Naziv = 'Zagreb');
INSERT INTO Grad (Naziv) SELECT 'Split'  WHERE NOT EXISTS (SELECT 1 FROM Grad WHERE Naziv = 'Split');
INSERT INTO Grad (Naziv) SELECT 'Rijeka' WHERE NOT EXISTS (SELECT 1 FROM Grad WHERE Naziv = 'Rijeka');

-- Tri stvarna hrvatska influencera kao default podaci. Broj pratitelja/engagement su
-- priblizne, ilustrativne vrijednosti za demo bazu (Igor Belan je stvaran broj pretplatnika
-- s njegovog YouTube kanala, ostalo je ilustrativno, nije uzivo ocitano sa stvarnih profila).
INSERT INTO Influencer (ImeNadimak, BrojPratitelja, EngagementRate, Zemlja, GradID, JezikSadrzaja, ProfilnaSlika)
SELECT 'Igor Belan', 109000, 4.2, 'Hrvatska', (SELECT IDGrad FROM Grad WHERE Naziv = 'Zagreb'), 'Hrvatski', ''
WHERE NOT EXISTS (SELECT 1 FROM Influencer WHERE ImeNadimak = 'Igor Belan');

INSERT INTO Influencer (ImeNadimak, BrojPratitelja, EngagementRate, Zemlja, GradID, JezikSadrzaja, ProfilnaSlika)
SELECT 'Marijana Batinić', 250000, 5.8, 'Hrvatska', (SELECT IDGrad FROM Grad WHERE Naziv = 'Split'), 'Hrvatski', ''
WHERE NOT EXISTS (SELECT 1 FROM Influencer WHERE ImeNadimak = 'Marijana Batinić');

INSERT INTO Influencer (ImeNadimak, BrojPratitelja, EngagementRate, Zemlja, GradID, JezikSadrzaja, ProfilnaSlika)
SELECT 'Antonija Blaće', 180000, 3.9, 'Hrvatska', (SELECT IDGrad FROM Grad WHERE Naziv = 'Rijeka'), 'Hrvatski', ''
WHERE NOT EXISTS (SELECT 1 FROM Influencer WHERE ImeNadimak = 'Antonija Blaće');

-- Platforme/nise/tipovi sadrzaja za default influencere (M:N veze).
INSERT INTO InfluencerPlatforma (IDInfluencer, IDPlatforma)
SELECT i.IDInfluencer, p.IDPlatforma FROM Influencer i, Platforma p
WHERE i.ImeNadimak = 'Igor Belan' AND p.Naziv = 'Instagram'
AND NOT EXISTS (SELECT 1 FROM InfluencerPlatforma ip WHERE ip.IDInfluencer = i.IDInfluencer AND ip.IDPlatforma = p.IDPlatforma);

INSERT INTO InfluencerPlatforma (IDInfluencer, IDPlatforma)
SELECT i.IDInfluencer, p.IDPlatforma FROM Influencer i, Platforma p
WHERE i.ImeNadimak = 'Marijana Batinić' AND p.Naziv = 'Instagram'
AND NOT EXISTS (SELECT 1 FROM InfluencerPlatforma ip WHERE ip.IDInfluencer = i.IDInfluencer AND ip.IDPlatforma = p.IDPlatforma);

INSERT INTO InfluencerPlatforma (IDInfluencer, IDPlatforma)
SELECT i.IDInfluencer, p.IDPlatforma FROM Influencer i, Platforma p
WHERE i.ImeNadimak = 'Marijana Batinić' AND p.Naziv = 'YouTube'
AND NOT EXISTS (SELECT 1 FROM InfluencerPlatforma ip WHERE ip.IDInfluencer = i.IDInfluencer AND ip.IDPlatforma = p.IDPlatforma);

INSERT INTO InfluencerPlatforma (IDInfluencer, IDPlatforma)
SELECT i.IDInfluencer, p.IDPlatforma FROM Influencer i, Platforma p
WHERE i.ImeNadimak = 'Antonija Blaće' AND p.Naziv = 'YouTube'
AND NOT EXISTS (SELECT 1 FROM InfluencerPlatforma ip WHERE ip.IDInfluencer = i.IDInfluencer AND ip.IDPlatforma = p.IDPlatforma);

INSERT INTO InfluencerNisa (IDInfluencer, IDNisa)
SELECT i.IDInfluencer, n.IDNisa FROM Influencer i, Nisa n
WHERE i.ImeNadimak = 'Igor Belan' AND n.Naziv = 'Gaming'
AND NOT EXISTS (SELECT 1 FROM InfluencerNisa ini WHERE ini.IDInfluencer = i.IDInfluencer AND ini.IDNisa = n.IDNisa);

INSERT INTO InfluencerNisa (IDInfluencer, IDNisa)
SELECT i.IDInfluencer, n.IDNisa FROM Influencer i, Nisa n
WHERE i.ImeNadimak = 'Marijana Batinić' AND n.Naziv = 'Fitness'
AND NOT EXISTS (SELECT 1 FROM InfluencerNisa ini WHERE ini.IDInfluencer = i.IDInfluencer AND ini.IDNisa = n.IDNisa);

INSERT INTO InfluencerNisa (IDInfluencer, IDNisa)
SELECT i.IDInfluencer, n.IDNisa FROM Influencer i, Nisa n
WHERE i.ImeNadimak = 'Antonija Blaće' AND n.Naziv = 'Lifestyle'
AND NOT EXISTS (SELECT 1 FROM InfluencerNisa ini WHERE ini.IDInfluencer = i.IDInfluencer AND ini.IDNisa = n.IDNisa);

INSERT INTO InfluencerTipSadrzaja (IDInfluencer, IDTipSadrzaja)
SELECT i.IDInfluencer, t.IDTipSadrzaja FROM Influencer i, TipSadrzaja t
WHERE i.ImeNadimak = 'Igor Belan' AND t.Naziv = 'Reels'
AND NOT EXISTS (SELECT 1 FROM InfluencerTipSadrzaja iti WHERE iti.IDInfluencer = i.IDInfluencer AND iti.IDTipSadrzaja = t.IDTipSadrzaja);

INSERT INTO InfluencerTipSadrzaja (IDInfluencer, IDTipSadrzaja)
SELECT i.IDInfluencer, t.IDTipSadrzaja FROM Influencer i, TipSadrzaja t
WHERE i.ImeNadimak = 'Marijana Batinić' AND t.Naziv = 'Tutoriali'
AND NOT EXISTS (SELECT 1 FROM InfluencerTipSadrzaja iti WHERE iti.IDInfluencer = i.IDInfluencer AND iti.IDTipSadrzaja = t.IDTipSadrzaja);

INSERT INTO InfluencerTipSadrzaja (IDInfluencer, IDTipSadrzaja)
SELECT i.IDInfluencer, t.IDTipSadrzaja FROM Influencer i, TipSadrzaja t
WHERE i.ImeNadimak = 'Antonija Blaće' AND t.Naziv = 'Recenzije'
AND NOT EXISTS (SELECT 1 FROM InfluencerTipSadrzaja iti WHERE iti.IDInfluencer = i.IDInfluencer AND iti.IDTipSadrzaja = t.IDTipSadrzaja);

-- Primjer brand suradnje kao default podatak, da ekran ne bude prazan pri prvom pokretanju.
INSERT INTO BrandSuradnja (NazivKampanje, BrandID, Godina, Status)
SELECT 'Dummy suradnja', (SELECT IDBrand FROM Brand WHERE Naziv = 'Nike'), 2026, 'PLANIRANA'
WHERE NOT EXISTS (SELECT 1 FROM BrandSuradnja WHERE NazivKampanje = 'Dummy suradnja');

INSERT INTO BrandSuradnjaInfluencer (IDBrandSuradnja, IDInfluencer)
SELECT bs.IDBrandSuradnja, i.IDInfluencer FROM BrandSuradnja bs, Influencer i
WHERE bs.NazivKampanje = 'Dummy suradnja' AND i.ImeNadimak = 'Igor Belan'
AND NOT EXISTS (SELECT 1 FROM BrandSuradnjaInfluencer bsi WHERE bsi.IDBrandSuradnja = bs.IDBrandSuradnja AND bsi.IDInfluencer = i.IDInfluencer);
