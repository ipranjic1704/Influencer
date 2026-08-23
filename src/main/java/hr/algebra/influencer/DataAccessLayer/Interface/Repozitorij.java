package hr.algebra.influencer.DataAccessLayer.Interface;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Repozitorij<T> {

    List<T> getAll();

    Optional<T> getById(int id);

    void create(T entitet);

    void update(T entitet);

    void delete(int id);

    default boolean exists(int id) {
        return getById(id).isPresent();
    }

    default long count() {
        return getAll().size();
    }

    default List<T> findWhere(Predicate<T> uvjet) {
        return getAll().stream().filter(uvjet).collect(Collectors.toList());
    }

    default boolean isDuplicate(Function<T, String> kljuc, T entitet) {
        Map<String, T> postojeci = getAll().stream()
                .collect(Collectors.toMap(e -> kljuc.apply(e).toLowerCase().trim(), e -> e, (a, b) -> a));
        return postojeci.containsKey(kljuc.apply(entitet).toLowerCase().trim());
    }
}
