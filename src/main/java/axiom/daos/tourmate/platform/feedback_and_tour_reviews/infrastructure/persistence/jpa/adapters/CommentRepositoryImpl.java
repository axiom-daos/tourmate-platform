
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
 */
@Repository
@Transactional(readOnly = true)
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentPersistenceRepository persistenceRepository;

    public CommentRepositoryImpl(
            CommentPersistenceRepository persistenceRepository
    ) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return persistenceRepository.findById(id)
                .map(CommentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Comment> findAll() {
        return persistenceRepository.findAll()
                .stream()
                .map(CommentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Comment> findByReviewId(Long reviewId) {
        return persistenceRepository.findByReviewId(reviewId)
                .stream()
                .map(CommentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    /**
     * Guarda un comentario nuevo o actualiza su contenido.
     */
    @Override
    @Transactional
    public Comment save(Comment comment) {

        Objects.requireNonNull(
                comment,
                "El comentario no puede ser nulo."
        );

        CommentPersistenceEntity entity;

        if (comment.getId() == null) {

            // Nuevo comentario.
            entity = CommentPersistenceAssembler
                    .toPersistenceFromDomain(comment);

        } else {

            // Comentario existente.
            CommentPersistenceEntity existingEntity =
                    persistenceRepository.findById(comment.getId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "No existe el comentario con ID: "
                                                    + comment.getId()
                                    )
                            );

            entity = CommentPersistenceAssembler
                    .updatePersistenceFromDomain(
                            comment,
                            existingEntity
                    );
        }

        CommentPersistenceEntity savedEntity =
                persistenceRepository.saveAndFlush(entity);

        return CommentPersistenceAssembler
                .toDomainFromPersistence(savedEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        persistenceRepository.deleteById(id);
    }

    /**
     * Elimina los comentarios relacionados con una reseña.
     */
    @Override
    @Transactional
    public void deleteByReviewId(Long reviewId) {
        persistenceRepository.deleteByReviewId(reviewId);
    }
}
