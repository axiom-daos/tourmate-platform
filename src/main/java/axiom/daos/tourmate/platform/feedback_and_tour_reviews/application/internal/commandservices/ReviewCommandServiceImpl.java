
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.ReviewCommandService;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de comandos de reseñas.
 *
 * Contiene la lógica de aplicación necesaria
 * para crear, actualizar y eliminar reseñas.
 *
 * Utiliza los repositorios del dominio
 * sin depender directamente de JPA.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Service
public class ReviewCommandServiceImpl implements ReviewCommandService {

    // Repositorio para gestionar las reseñas.
    private final ReviewRepository reviewRepository;

    // Repositorio para gestionar los comentarios.
    private final CommentRepository commentRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * Spring proporcionará las implementaciones
     * de los repositorios cuando estén disponibles.
     */
    public ReviewCommandServiceImpl(
            ReviewRepository reviewRepository,
            CommentRepository commentRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Procesa la creación de una nueva reseña.
     *
     * Verifica que el usuario no tenga otra
     * reseña registrada para el mismo tour.
     *
     * @param command Datos de creación.
     * @return Reseña creada o error de aplicación.
     */
    @Override
    public Result<Review, ApplicationError> handle(
            CreateReviewCommand command
    ) {

        try {
            // Comprobamos si el usuario ya calificó este tour.
            boolean alreadyReviewed =
                    reviewRepository.existsByUserIdAndTourId(
                            command.userId(),
                            command.tourId()
                    );

            // Evitamos que el mismo usuario registre
            // más de una reseña para el mismo tour.
            if (alreadyReviewed) {
                return Result.failure(
                        ApplicationError.conflict(
                                "Review",
                                "El usuario ya registró una reseña para este tour."
                        )
                );
            }

            // Creamos el agregado del dominio.
            Review review = new Review(
                    command.userId(),
                    command.tourId(),
                    command.rating()
            );

            // Guardamos la reseña mediante el repositorio.
            Review savedReview = reviewRepository.save(review);

            // Devolvemos la reseña almacenada.
            return Result.success(savedReview);

        } catch (IllegalArgumentException exception) {

            // Convertimos errores de validación
            // en errores de aplicación.
            return Result.failure(
                    ApplicationError.validationError(
                            "Review",
                            exception.getMessage()
                    )
            );

        } catch (RuntimeException exception) {

            // Informamos de un fallo inesperado
            // durante la creación.
            return Result.failure(
                    ApplicationError.unexpected(
                            "create-review",
                            exception.toString()
                    )
            );
        }
    }

    /**
     * Procesa la actualización de una reseña.
     *
     * Comprueba que exista y evita que la edición
     * produzca una reseña duplicada.
     *
     * @param command Datos de actualización.
     * @return Reseña actualizada o error.
     */
    @Override
    public Result<Review, ApplicationError> handle(
            UpdateReviewCommand command
    ) {

        try {
            // Buscamos la reseña que se desea actualizar.
            var reviewOptional =
                    reviewRepository.findById(command.reviewId());

            // Verificamos que realmente exista.
            if (reviewOptional.isEmpty()) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                command.reviewId().toString()
                        )
                );
            }

            // Comprobamos que no exista otra reseña
            // del mismo usuario para el mismo tour.
            boolean duplicated =
                    reviewRepository.existsByUserIdAndTourIdAndIdNot(
                            command.userId(),
                            command.tourId(),
                            command.reviewId()
                    );

            if (duplicated) {
                return Result.failure(
                        ApplicationError.conflict(
                                "Review",
                                "Ya existe otra reseña del usuario para este tour."
                        )
                );
            }

            // Recuperamos el agregado existente.
            Review review = reviewOptional.get();

            // Actualizamos sus datos respetando
            // las validaciones del dominio.
            review.updateInformation(
                    command.userId(),
                    command.tourId(),
                    command.rating()
            );

            // Persistimos la información actualizada.
            Review updatedReview = reviewRepository.save(review);

            // Devolvemos el resultado exitoso.
            return Result.success(updatedReview);

        } catch (IllegalArgumentException exception) {

            // Capturamos errores relacionados
            // con las reglas de validación.
            return Result.failure(
                    ApplicationError.validationError(
                            "Review",
                            exception.getMessage()
                    )
            );

        } catch (RuntimeException exception) {

            // Capturamos fallos inesperados
            // durante la actualización.
            return Result.failure(
                    ApplicationError.unexpected(
                            "update-review",
                            exception.toString()
                    )
            );
        }
    }

    /**
     * Procesa la eliminación de una reseña.
     *
     * Primero elimina sus comentarios asociados
     * y después elimina la reseña.
     *
     * La transacción garantiza que ambas
     * operaciones se completen juntas.
     *
     * @param command Identificador de la reseña.
     * @return true si fue eliminada o un error.
     */
    @Override
    @Transactional
    public Result<Boolean, ApplicationError> handle(
            DeleteReviewCommand command
    ) {

        try {
            // Verificamos si la reseña existe.
            boolean exists =
                    reviewRepository.existsById(command.reviewId());

            if (!exists) {
                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                command.reviewId().toString()
                        )
                );
            }

            // Eliminamos primero los comentarios
            // relacionados con la reseña.
            commentRepository.deleteByReviewId(
                    command.reviewId()
            );

            // Eliminamos la reseña solicitada.
            reviewRepository.deleteById(
                    command.reviewId()
            );

            // Indicamos que la eliminación fue exitosa.
            return Result.success(true);

        } catch (RuntimeException exception) {

            // Como devolvemos un Result en lugar
            // de propagar la excepción, marcamos
            // la transacción para revertir los cambios.
            org.springframework.transaction.interceptor
                    .TransactionAspectSupport
                    .currentTransactionStatus()
                    .setRollbackOnly();

            return Result.failure(
                    ApplicationError.unexpected(
                            "delete-review",
                            exception.toString()
                    )
            );
        }
    }
}
