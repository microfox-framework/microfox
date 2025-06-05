package ir.moke.microfox.db.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Map;

public interface CriteriaProvider<T> {
    Predicate execute(CriteriaBuilder cb, Root<T> root, Map<String, Object> queryParams);
}
