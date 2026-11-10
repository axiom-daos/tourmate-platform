
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.queryservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.CommentQueryService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllCommentsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentsByReviewIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio encargado de consultar los comentarios.
 */
@Service
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;

    public CommentQueryServiceImpl(
            CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }

    /**
     * Obtiene todos los comentarios registrados.
     */
    @Override
    public List<Comment> handle(
            GetAllCommentsQuery query
    ) {

        Objects.requireNonNull(
                query,
                "La consulta de comentarios es obligatoria."
        );

        return commentRepository.findAll();
    }

    /**
     * Busca un comentario por su identificador.
     */
    @Override
    public Optional<Comment> handle(
            GetCommentByIdQuery query
    ) {

        Objects.requireNonNull(
                query,
                "La consulta del comentario es obligatoria."
        );

        return commentRepository.findById(
                query.commentId()
        );
    }

    /**
     * Obtiene los comentarios de una reseña específica.
     */
    @Override
    public List<Comment> handle(
            GetCommentsByReviewIdQuery query
    ) {

        Objects.requireNonNull(
                query,
                "La consulta de comentarios por reseña es obligatoria."
        );

        return commentRepository.findByReviewId(
                query.reviewId()
        );
    }
}
