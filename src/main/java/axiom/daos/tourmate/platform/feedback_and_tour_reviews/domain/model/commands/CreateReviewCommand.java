package axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands;

/**
 * Comando para registrar una nueva reseña turística.
 *
 * Representa los datos necesarios para que
 * un usuario califique un tour.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param userId Identificador del usuario.
 * @param tourId Identificador del tour.
 * @param rating Calificación entre 1 y 5.
 */
public record CreateReviewCommand(
        Long userId,
        Long tourId,
        Integer rating
) {

    /**
     * Constructor compacto del comando.
     *
     * Valida los datos antes de permitir
     * la creación de una nueva reseña.
     *
     * @throws IllegalArgumentException
     * si alguno de los datos es inválido.
     */
    public CreateReviewCommand {

        // El usuario debe tener un ID válido.
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del usuario debe ser mayor que cero."
            );
        }

        // El tour debe tener un ID válido.
        if (tourId == null || tourId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del tour debe ser mayor que cero."
            );
        }

        // La calificación debe estar entre 1 y 5.
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 5."
            );
        }
    }
}
