
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.adapters;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.assemblers.CommentPersistenceAssembler;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.entities.CommentPersistenceEntity;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.infrastructure.persistence.jpa.repositories.CommentPersistenceRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adaptador de persistencia para los comentarios.
 *
 * Implementa el contrato CommentRepository del dominio
 * utilizando Spring Data JPA.
 *
 * Convierte las entidades de persistencia en objetos
 * del dominio mediante CommentPersistenceAssembler.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Repository
@Transactional(readOnly = true)
public class CommentRepositoryImpl implements CommentRepository {

    // Repositorio JPA encargado de acceder a
    // los registros de la tabla comments.
    private final CommentPersistenceRepository persistenceRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * Spring proporciona automáticamente
     * la implementación del repositorio JPA.
     *
     * @param persistenceRepository Repositorio JPA.
     */
    public CommentRepositoryImpl(
            CommentPersistenceRepository persistenceRepository
    ) {
        this.persistenceRepository = persistenceRepository;
    }

    /**
     * Busca un comentario mediante su identificador.
     *
     * @param id Identificador del comentario.
     * @return Comentario encontrado o un Optional vacío.
     */
    @Override
    public Optional<Comment> findById(Long id) {

        // Buscamos el registro en PostgreSQL
        // y lo convertimos en una entidad de dominio.
        return persistenceRepository.findById(id)
                .map(
                        CommentPersistenceAssembler
                                ::toDomainFromPersistence
                );
    }

    /**
     * Obtiene todos los comentarios registrados.
     *
     * @return Lista de comentarios del dominio.
     */
    @Override
    public List<Comment> findAll() {

        // Recuperamos los registros y convertimos
        // cada entidad JPA en un objeto Comment.
        return persistenceRepository.findAll()
                .stream()
                .map(
                        CommentPersistenceAssembler
                                ::toDomainFromPersistence
                )
                .toList();
    }

    /**
     * Obtiene los comentarios relacionados
     * con una reseña específica.
     *
     * @param reviewId Identificador de la reseña.
     * @return Lista de comentarios de la reseña.
     */
    @Override
    public List<Comment> findByReviewId(Long reviewId) {

        // Consultamos los comentarios asociados
        // al identificador de la reseña.
        return persistenceRepository.findByReviewId(reviewId)
                .stream()
                .map(
                        CommentPersistenceAssembler
                                ::toDomainFromPersistence
                )
                .toList();
    }

    /**
     * Comprueba si existe un comentario.
     *
     * @param id Identificador del comentario.
     * @return true cuando existe; false en caso contrario.
     */
    @Override
    public boolean existsById(Long id) {

        // Delegamos la comprobación al repositorio JPA.
        return persistenceRepository.existsById(id);
    }

    /**
     * Guarda un comentario nuevo o actualiza
     * uno existente en PostgreSQL.
     *
     * @param comment Entidad del dominio.
     * @return Comentario almacenado.
     */
    @Override
    @Transactional
    public Comment save(Comment comment) {

        // Verificamos que el comentario no sea nulo.
        Objects.requireNonNull(
                comment,
                "El comentario no puede ser nulo."
        );

        CommentPersistenceEntity entity;

        // Si no tiene ID, estamos creando un comentario.
        if (comment.getId() == null) {

            // Convertimos el comentario nuevo
            // en una entidad de persistencia.
            entity = CommentPersistenceAssembler
                    .toPersistenceFromDomain(comment);

        } else {

            // Buscamos el comentario existente.
            CommentPersistenceEntity existingEntity =
                    persistenceRepository.findById(comment.getId())
                            .orElseThrow(
                                    () -> new IllegalArgumentException(
                                            "No existe el comentario con ID: "
                                                    + comment.getId()
                                    )
                            );

            // Aplicamos los cambios del dominio
            // sobre el registro recuperado.
            entity = CommentPersistenceAssembler
                    .updatePersistenceFromDomain(
                            comment,
                            existingEntity
                    );
        }

        // Guardamos y sincronizamos el registro
        // con la base de datos.
        CommentPersistenceEntity savedEntity =
                persistenceRepository.saveAndFlush(entity);

        // Convertimos el registro guardado
        // nuevamente en una entidad del dominio.
        return CommentPersistenceAssembler
                .toDomainFromPersistence(savedEntity);
    }

    /**
     * Elimina un comentario mediante su ID.
     *
     * @param id Identificador del comentario.
     */
    @Override
    @Transactional
    public void deleteById(Long id) {

        // Solicitamos la eliminación del comentario.
        persistenceRepository.deleteById(id);
    }

    /**
     * Elimina todos los comentarios asociados
     * a una reseña determinada.
     *
     * Se utiliza especialmente cuando
     * una reseña es eliminada.
     *
     * @param reviewId Identificador de la reseña.
     */
    @Override
    @Transactional
    public void deleteByReviewId(Long reviewId) {

        // Eliminamos los comentarios relacionados
        // mediante el repositorio JPA.
        persistenceRepository.deleteByReviewId(reviewId);
    }
}
