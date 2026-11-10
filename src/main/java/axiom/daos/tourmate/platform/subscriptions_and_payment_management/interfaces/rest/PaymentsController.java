package axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;
import axiom.daos.tourmate.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.commandservices.PaymentCommandService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.application.queryservices.PaymentQueryService;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.aggregates.Payment;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetAllPaymentsQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPaymentByIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.domain.model.queries.GetPaymentsByAgencyIdQuery;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.CreatePaymentResource;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.resources.PaymentResource;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform.CreatePaymentCommandFromResourceAssembler;
import axiom.daos.tourmate.platform.subscriptions_and_payment_management.interfaces.rest.transform.PaymentResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for recording and querying subscription payment transactions.
 */
@RestController
@RequestMapping(value = "/api/v1/payments", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payments", description = "Subscription payment transaction ledger endpoints")
public class PaymentsController {

    private final PaymentCommandService paymentCommandService;
    private final PaymentQueryService paymentQueryService;

    public PaymentsController(
            PaymentCommandService paymentCommandService,
            PaymentQueryService paymentQueryService
    ) {
        this.paymentCommandService = paymentCommandService;
        this.paymentQueryService = paymentQueryService;
    }

    /**
     * Record a new subscription payment transaction.
     */
    @PostMapping
    @Operation(summary = "Record a payment transaction", description = "Registers a settled payment transaction voucher for an agency subscription.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment recorded successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Plan not found")
    })
    public ResponseEntity<?> recordPayment(@RequestBody CreatePaymentResource resource) {
        var command = CreatePaymentCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = paymentCommandService.handle(command)
                .flatMap(paymentId -> paymentQueryService.handle(new GetPaymentByIdQuery(paymentId))
                        .<Result<Payment, ApplicationError>>map(Result::success)
                        .orElseGet(() -> Result.failure(ApplicationError.notFound("Payment", paymentId.toString()))));

        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                PaymentResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieve all payments, optionally filtered by agencyId.
     */
    @GetMapping
    @Operation(summary = "Get payment transactions", description = "Returns all recorded payment transactions, or filtered by agency id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    })
    public ResponseEntity<List<PaymentResource>> getPayments(
            @Parameter(description = "Optional agency identifier to filter payment ledger")
            @RequestParam(required = false) Long agencyId
    ) {
        List<Payment> payments;
        if (agencyId != null) {
            payments = paymentQueryService.handle(new GetPaymentsByAgencyIdQuery(agencyId));
        } else {
            payments = paymentQueryService.handle(new GetAllPaymentsQuery());
        }

        var resources = payments.stream()
                .map(PaymentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieve payment details by identifier.
     */
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by id", description = "Returns details for a specific payment ledger transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResource.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResource> getPaymentById(
            @Parameter(description = "Payment identifier", required = true) @PathVariable Long paymentId
    ) {
        var payment = paymentQueryService.handle(new GetPaymentByIdQuery(paymentId));
        return payment.map(p -> ResponseEntity.ok(PaymentResourceFromEntityAssembler.toResourceFromEntity(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
