
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.assemblers;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.CommentPersistenceEntity;

import java.util.Objects;

/**
 * Ensamblador de persistencia para los comentarios.
 *
 * Convierte objetos entre la entidad de dominio
 * Comment y la entidad JPA CommentPersistenceEntity.
 *
 * Permite mantener el dominio independiente
 * de las tecnologías de persistencia.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public final class CommentPersistenceAssembler {

    /**
     * Constructor privado.
     *
     * Evita crear instancias de esta clase,
     * ya que todos sus métodos son estáticos.
     */
    private CommentPersistenceAssembler() {
    }

    /**
     * Convierte una entidad JPA en una entidad
     * de dominio Comment.
     *
     * Se utiliza al recuperar comentarios
     * almacenados en PostgreSQL.
     *
     * @param entity Entidad de persistencia JPA.
     * @return Comentario reconstruido del dominio.
     */
    public static Comment toDomainFromPersistence(
            CommentPersistenceEntity entity
    ) {

        // Si no existe una entidad, devolvemos null.
        if (entity == null) {
            return null;
        }

        // Comprobamos que exista la fecha de creación.
        Objects.requireNonNull(
                entity.getCreatedAt(),
                "El comentario almacenado no tiene fecha de creación."
        );

        // Reconstruimos el comentario con sus datos
        // originales, incluyendo ID y fecha.
        return Comment.reconstitute(
                entity.getId(),
                entity.getReviewId(),
                entity.getContent(),
                entity.getCreatedAt().toInstant()
        );
    }

    /**
     * Convierte un comentario nuevo del dominio
     * en una entidad de persistencia JPA.
     *
     * Esta operación se utiliza al registrar
     * un comentario por primera vez.
     *
     * @param comment Comentario del dominio.
     * @return Nueva entidad JPA.
     */
    public static CommentPersistenceEntity toPersistenceFromDomain(
            Comment comment
    ) {

        // Verificamos que el comentario no sea nulo.
        Objects.requireNonNull(
                comment,
                "El comentario no puede ser nulo."
        );

        // Los comentarios nuevos todavía no tienen ID.
        if (comment.getId() != null) {
            throw new IllegalArgumentException(
                    "Para actualizar un comentario existente, "
                            + "utiliza updatePersistenceFromDomain."
            );
        }

        // Creamos la entidad JPA con los datos
        // necesarios para almacenar el comentario.
        return new CommentPersistenceEntity(
                comment.getReviewId(),
                comment.getContent()
        );
    }

    /**
     * Actualiza una entidad JPA existente
     * utilizando los datos del dominio.
     *
     * Conserva el identificador y las fechas
     * de auditoría administradas por JPA.
     *
     * @param comment Comentario actualizado.
     * @param entity Entidad JPA existente.
     * @return Entidad JPA con los cambios aplicados.
     */
    public static CommentPersistenceEntity updatePersistenceFromDomain(
            Comment comment,
            CommentPersistenceEntity entity
    ) {

        // Comprobamos que ambos objetos existan.
        Objects.requireNonNull(
                comment,
                "El comentario no puede ser nulo."
        );

        Objects.requireNonNull(
                entity,
                "La entidad de persistencia no puede ser nula."
        );

        // Verificamos que estemos actualizando
        // el mismo comentario.
        if (comment.getId() == null
                || !comment.getId().equals(entity.getId())) {

            throw new IllegalArgumentException(
                    "Los identificadores del comentario "
                            + "y la entidad JPA no coinciden."
            );
        }

        // Actualizamos los datos del comentario.
        entity.setReviewId(comment.getReviewId());
        entity.setContent(comment.getContent());

        // No modificamos el ID ni las fechas de auditoría.
        return entity;
    }
}
