package org.marketcetera.trade.jpa;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Specification utility class for building dynamic queries to replace QueryDSL BooleanBuilder.
 * 
 * <p>This class provides reusable specifications for common query operations.</p>
 * 
 * @author claude
 * @version $Id$
 */
public class ReportSpecifications {
    
    /**
     * Creates a specification for equality comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param value the value to compare against
     * @return a specification that compares the attribute to the given value
     */
    public static <T> Specification<T> equalTo(String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.isNull(root.get(attribute));
            }
            // Special handling for enum values to avoid ClassCastException in Jakarta EE
            if (value instanceof Enum) {
                return cb.equal(root.get(attribute), value.toString());
            }
            return cb.equal(root.get(attribute), value);
        };
    }
    
    /**
     * Creates a specification for "not equal to" comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param value the value to compare against
     * @return a specification that tests if the attribute is not equal to the given value
     */
    public static <T> Specification<T> notEqualTo(String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.isNotNull(root.get(attribute));
            }
            // Special handling for enum values to avoid ClassCastException in Jakarta EE
            if (value instanceof Enum) {
                return cb.notEqual(root.get(attribute), value.toString());
            }
            return cb.notEqual(root.get(attribute), value);
        };
    }
    
    /**
     * Creates a specification for "greater than or equal to" comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param value the value to compare against
     * @param <Y> the attribute type
     * @return a specification that tests if the attribute is greater than or equal to the given value
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThanOrEqualTo(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.isNull(root.get(attribute));
            }
            return cb.greaterThanOrEqualTo(root.get(attribute), value);
        };
    }
    
    /**
     * Creates a specification for "less than or equal to" comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param value the value to compare against
     * @param <Y> the attribute type
     * @return a specification that tests if the attribute is less than or equal to the given value
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThanOrEqualTo(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.isNull(root.get(attribute));
            }
            return cb.lessThanOrEqualTo(root.get(attribute), value);
        };
    }
    
    /**
     * Creates a specification for "greater than" comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param value the value to compare against
     * @param <Y> the attribute type
     * @return a specification that tests if the attribute is greater than the given value
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThan(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.isNull(root.get(attribute));
            }
            return cb.greaterThan(root.get(attribute), value);
        };
    }
    
    /**
     * Creates a specification for "less than" comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param value the value to compare against
     * @param <Y> the attribute type
     * @return a specification that tests if the attribute is less than the given value
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThan(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.isNull(root.get(attribute));
            }
            return cb.lessThan(root.get(attribute), value);
        };
    }
    
    /**
     * Creates a specification for "in" comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param values the collection of values to compare against
     * @return a specification that tests if the attribute is in the given collection of values
     */
    public static <T> Specification<T> in(String attribute, Collection<?> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return cb.conjunction(); // always true
            }
            Path<?> path = (Path<?>)root.get(attribute);
            return (Predicate)path.in(values);
        };
    }
    
    /**
     * Creates a specification for "not in" comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param values the collection of values to compare against
     * @return a specification that tests if the attribute is not in the given collection of values
     */
    @SuppressWarnings("unchecked")
    public static <T> Specification<T> notIn(String attribute, Collection<?> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return cb.conjunction(); // always true
            }
            Path<?> path = (Path<?>)root.get(attribute);
            return cb.not((Expression<Boolean>)path.in(values));
        };
    }
    
    /**
     * Creates a specification for checking if an attribute is null.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to check
     * @return a specification that tests if the attribute is null
     */
    public static <T> Specification<T> isNull(String attribute) {
        return (root, query, cb) -> cb.isNull(root.get(attribute));
    }
    
    /**
     * Creates a specification for checking if an attribute is not null.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to check
     * @return a specification that tests if the attribute is not null
     */
    public static <T> Specification<T> isNotNull(String attribute) {
        return (root, query, cb) -> cb.isNotNull(root.get(attribute));
    }
    
    /**
     * Creates a specification for a date range comparison.
     *
     * @param <T> the entity type
     * @param attribute the attribute name to compare
     * @param startDate the start date (inclusive), can be null
     * @param endDate the end date (inclusive), can be null
     * @return a specification that tests if the attribute is between the start and end dates
     */
    public static <T> Specification<T> dateBetween(String attribute, Date startDate, Date endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) {
                return cb.conjunction(); // always true
            }
            if (startDate == null) {
                return cb.lessThanOrEqualTo(root.get(attribute), endDate);
            }
            if (endDate == null) {
                return cb.greaterThanOrEqualTo(root.get(attribute), startDate);
            }
            return cb.between(root.get(attribute), startDate, endDate);
        };
    }
    
    /**
     * Creates a specification that is the conjunction (AND) of two specifications.
     *
     * @param <T> the entity type
     * @param spec1 the first specification
     * @param spec2 the second specification
     * @return a specification that is the conjunction of the two specifications
     */
    public static <T> Specification<T> and(Specification<T> spec1, Specification<T> spec2) {
        if (spec1 == null) {
            return spec2;
        }
        if (spec2 == null) {
            return spec1;
        }
        return Specification.where(spec1).and(spec2);
    }
    
    /**
     * Creates a specification that is the disjunction (OR) of two specifications.
     *
     * @param <T> the entity type
     * @param spec1 the first specification
     * @param spec2 the second specification
     * @return a specification that is the disjunction of the two specifications
     */
    public static <T> Specification<T> or(Specification<T> spec1, Specification<T> spec2) {
        if (spec1 == null) {
            return spec2;
        }
        if (spec2 == null) {
            return spec1;
        }
        return Specification.where(spec1).or(spec2);
    }
}