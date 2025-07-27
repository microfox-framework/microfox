package repository;

import entity.Person;
import ir.moke.microfox.jpa.CriteriaProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonCriteriaProvider implements CriteriaProvider<Person> {
    @Override
    public Predicate execute(CriteriaBuilder cb, Root<Person> root, Map<String, Object> queryParams) {
        List<Predicate> predicates = new ArrayList<>();

        if (queryParams.containsKey("id")) {
            Long id = (Long) queryParams.get("id");
            predicates.add(cb.equal(root.get("id"), id));
        }

        if (queryParams.containsKey("name")) {
            String name = (String) queryParams.get("name");
            predicates.add(cb.like(root.get("name"), "%" + name + "%"));
        }

        if (queryParams.containsKey("family")) {
            String family = (String) queryParams.get("family");
            predicates.add(cb.like(root.get("family"), "%" + family + "%"));
        }

        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
    }
}
