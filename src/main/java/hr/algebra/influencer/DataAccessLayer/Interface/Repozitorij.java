package hr.algebra.influencer.DataAccessLayer.Interface;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Repozitorij<T>
{

    List<T> getAll();

    Optional<T> getById(int id);

    void create(T entitet);

    void update(T entitet);

    void delete(int id);

    default boolean exists(int id)
    {
        return getById(id).isPresent();
    }

    default long count()
    {
        return getAll().size();
    }

    // Prima Predicate<T> kao parametar - pozivatelj odlucuje KOJI uvjet filtriranja koristi
    // (npr. influencer -> influencer.getZemlja().equals("Hrvatska")), a ova metoda samo primijeni taj uvjet
    // na getAll(). Jedna metoda ovdje pokriva beskonacno razlicitih filtera bez pisanja nove metode za svaki.
    default List<T> findWhere(Predicate<T> uvjet)
    {
        return getAll().stream().filter(uvjet).collect(Collectors.toList());
    }

    // kljuc: Function<T,String> koja iz entiteta izvuce polje po kojem se provjerava duplikat
    // (npr. Influencer::getImeNadimak). Mapa se gradi case-insensitive (toLowerCase/trim) da "Ivan" i "ivan "
    // broje kao isti unos, a containsKey provjerava postoji li vec entitet s istim kljucem prije spremanja.
    default boolean isDuplicate(Function<T, String> kljuc, T entitet)
    {
        Map<String, T> postojeci = getAll().stream()
                .collect(Collectors.toMap(e -> kljuc.apply(e).toLowerCase().trim(), e -> e, (a, b) -> a));
        return postojeci.containsKey(kljuc.apply(entitet).toLowerCase().trim());
    }
}
