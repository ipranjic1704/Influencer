package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.Model.Enum.Uloga;
import hr.algebra.influencer.Model.Korisnik;

public final class Session {

    private static Korisnik trenutniKorisnik;

    private Session() {
    }

    public static void login(Korisnik korisnik) {
        trenutniKorisnik = korisnik;
    }

    public static Korisnik getTrenutniKorisnik() {
        return trenutniKorisnik;
    }

    public static boolean isAdmin() {
        return trenutniKorisnik != null && trenutniKorisnik.getUloga() == Uloga.ADMIN;
    }

    public static boolean isBrend() {
        return trenutniKorisnik != null && trenutniKorisnik.getUloga() == Uloga.BREND;
    }

    public static boolean isInfluencer() {
        return trenutniKorisnik != null && trenutniKorisnik.getUloga() == Uloga.INFLUENCER;
    }

    public static boolean smijeDodatiSuradnju() {
        return trenutniKorisnik != null && trenutniKorisnik.getUloga().smijeDodatiSuradnju();
    }

    public static void logout() {
        trenutniKorisnik = null;
    }
}
