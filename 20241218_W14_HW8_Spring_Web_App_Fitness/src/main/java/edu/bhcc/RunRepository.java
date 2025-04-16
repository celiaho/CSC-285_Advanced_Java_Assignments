package edu.bhcc;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Run Create/Read/Delete Repo.
 * The `RunRepository` interface extends `CrudRepository` from Spring Data JPA.
 * It provides basic CRUD (Create, Read, Update, Delete) operations for the `Run` entity
 * along with custom finder methods for searching by route, miles (greater than or equal to,
 * less than or equal to), and ID.
 */
public interface RunRepository extends CrudRepository<Run, Long> {
    /**
     * Find by ID.
     */
    Run findById(long id);

    // For future iterations

    /**
     * Find by Route.
     */
    List<Run> findByRoute(String route);

    /**
     * Find by Miles Greater Than or Equal To.
     */
    List<Run> findByMilesGreaterThanEqual(double miles);

    /**
     * Find by Miles Less Than or Equal To.
     */
    List<Run> findByMilesLessThanEqual(double miles);
}