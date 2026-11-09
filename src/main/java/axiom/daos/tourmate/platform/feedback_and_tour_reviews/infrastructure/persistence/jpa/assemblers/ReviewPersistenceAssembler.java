
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.ReviewPersistenceEntity;

import java.util.Objects;

/**
 * Ensamblador de persistencia para las reseñas.
 *
 * Convierte objetos entre el dominio y
 * la infraestructura de persistencia.
 *
 * Esta separación permite mantener el agregado
 * Review independiente de las anotaciones JPA.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public final class ReviewPersistenceAssembler {

    /**
     * Constructor privado.
     *
     * Impide crear instancias del ensamblador,
     * porque sus métodos son estáticos.
     */
    private ReviewPersistenceAssembler() {
    }

    /**
     * Convierte una entidad JPA en un agregado
     * del dominio Review.
     *
     * Este método se utilizará al recuperar
     * una reseña desde PostgreSQL.
     *
     * @param entity Entidad de persistencia.
     * @return Agregado Review reconstruido.
     */
    public static Review toDomainFromPersistence(
            ReviewPersistenceEntity entity
    ) {

        // Si la entidad es nula, devolvemos null.
        if (entity == null) {
            return null;
        }

        // Validamos que la fecha de auditoría exista.
        Objects.requireNonNull(
                entity.getCreatedAt(),
                "La reseña almacenada no tiene fecha de creación."
        );

        // Reconstruimos el agregado utilizando
        // los valores almacenados en PostgreSQL.
        return Review.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getTourId(),
                entity.getRating(),
                entity.getCreatedAt().toInstant()
        );
    }

    /**
     * Convierte un agregado nuevo en una entidad JPA.
     *
     * Se utiliza para preparar la persistencia
     * de una reseña que todavía no tiene ID.
     *
     * @param review Agregado del dominio.
     * @return Nueva entidad de persistencia.
     */
    public static ReviewPersistenceEntity toPersistenceFromDomain(
            Review review
    ) {

        // Comprobamos que el agregado exista.
        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        // Este método solamente prepara nuevas reseñas.
        // Para actualizar una existente se debe utilizar
        // updatePersistenceFromDomain().
        if (review.getId() != null) {
            throw new IllegalArgumentException(
                    "Para actualizar una reseña existente, "
                            + "utiliza updatePersistenceFromDomain."
            );
        }

        // Construimos la entidad de persistencia.
        // JPA asignará el ID al guardar el registro.
        return new ReviewPersistenceEntity(
                review.getUserId(),
                review.getTourId(),
                review.getRating()
        );
    }

    /**
     * Copia los cambios del agregado de dominio
     * hacia una entidad JPA ya existente.
     *
     * Utilizamos esta operación para conservar
     * los campos de auditoría administrados por JPA,
     * especialmente la fecha original de creación.
     *
     * @param review Agregado actualizado.
     * @param entity Entidad JPA existente.
     * @return Entidad JPA con los nuevos valores.
     */
    public static ReviewPersistenceEntity updatePersistenceFromDomain(
            Review review,
            ReviewPersistenceEntity entity
    ) {

        // Validamos los argumentos recibidos.
        Objects.requireNonNull(
                review,
                "La reseña no puede ser nula."
        );

        Objects.requireNonNull(
                entity,
                "La entidad de persistencia no puede ser nula."
        );

        // Verificamos que los identificadores
        // correspondan a la misma reseña.
        if (review.getId() == null
                || !review.getId().equals(entity.getId())) {

            throw new IllegalArgumentException(
                    "Los identificadores de la reseña "
                            + "y la entidad JPA no coinciden."
            );
        }

        // Actualizamos únicamente los datos de negocio.
        entity.setUserId(review.getUserId());
        entity.setTourId(review.getTourId());
        entity.setRating(review.getRating());

        // No modificamos el ID ni las fechas de auditoría.
        return entity;
    }
}
