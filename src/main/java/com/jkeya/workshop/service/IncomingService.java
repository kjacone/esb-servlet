package com.jkeya.workshop.service;



// import java.time.Duration;
// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
import java.util.List;
// import java.util.Random;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

// import org.bson.types.ObjectId;
// import org.eclipse.microprofile.faulttolerance.Fallback;
// import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.reactive.messaging.Channel;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

import io.quarkus.logging.Log;
import com.jkeya.workshop.Incoming;
import com.jkeya.workshop.Outgoing;
import com.jkeya.workshop.mapping.IncomingFullUpdateMapper;
import com.jkeya.workshop.mapping.IncomingPartialUpdateMapper;
import com.jkeya.workshop.mapping.IncomingToMDBMapper;
import com.jkeya.workshop.repository.IncomingRepository;

import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.TracingMetadata;

// import static javax.transaction.Transactional.TxType.REQUIRED;
// import static javax.transaction.Transactional.TxType.SUPPORTS;


/**
 * Service class containing business methods for the application.
 */
@ApplicationScoped
public class IncomingService {

  private final IncomingRepository IncomingRepository;
  private final Validator validator;
  private final IncomingPartialUpdateMapper IncomingPartialUpdateMapper;
  private final IncomingFullUpdateMapper IncomingFullUpdateMapper;
  private final IncomingToMDBMapper IncomingToMDBMapper;
	private final MutinyEmitter<com.jkeya.workshop.MdbRequest> emitter;


  public IncomingService(
    IncomingRepository incomingRepository,
    Validator validator,
    IncomingPartialUpdateMapper incomingPartialUpdateMapper,
    IncomingFullUpdateMapper incomingFullUpdateMapper,
    @Channel("mdbrequest") MutinyEmitter<com.jkeya.workshop.MdbRequest> emitter,
    // IncomingConfig incomingConfig,
    IncomingToMDBMapper incomingToMDBMapper) {
  
      this.emitter = emitter;
    this.IncomingRepository = incomingRepository;
    this.validator = validator;
    this.IncomingPartialUpdateMapper = incomingPartialUpdateMapper;
    this.IncomingFullUpdateMapper = incomingFullUpdateMapper;
    this.IncomingToMDBMapper = incomingToMDBMapper;
  }

  @WithSpan("IncomingService.findAllIncomingRequest")
  public Uni<List<Incoming>> findAllIncomingRequest() {
    Log.debug("Getting all Incominges");
    return this.IncomingRepository.listAll();
  }

  @WithSpan("IncomingService.findAllIncomingRequestHavingName")
  public Uni<List<Incoming>> findAllIncomingRequestHavingName(
    @SpanAttribute("arg.name") String name
  ) {
    Log.debugf("Finding all Incominges having name = %s", name);
    return this.IncomingRepository.listAllWhereNameLike(name);
  }

  @WithSpan("IncomingService.findIncomingByTransactionId")
  public Uni<Incoming> findIncomingByTransactionId(
    @SpanAttribute("arg.id") Long id
  ) {
    Log.debugf("Finding Incoming by id = %d", id);
    return this.IncomingRepository.findById(id);
  }

  @ReactiveTransactional
  @WithSpan("IncomingService.saveIncomingRequest")
  public Uni<Incoming> saveIncomingRequest(
    @SpanAttribute("arg.Incoming") Incoming incoming
  ) {
    Log.debugf("saveIncomingRequest Incoming: %s", incoming);
    return this.IncomingRepository.persist(incoming);
  }

  @ReactiveTransactional
  @WithSpan("IncomingService.replaceIncoming")
  public Uni<Incoming> replaceIncoming(
    @SpanAttribute("arg.Incoming") @NotNull @Valid Incoming Incoming
  ) {
    Log.debugf("Replacing Incoming: %s", Incoming);
    return this.IncomingRepository.findById(Incoming.getId())
      .onItem()
      .ifNotNull()
      .transform(h -> {
        this.IncomingFullUpdateMapper.mapFullUpdate(Incoming, h);
        return h;
      });
  }

  @ReactiveTransactional
  @WithSpan("IncomingService.partialUpdateIncomingRequest")
  public Uni<Incoming> partialUpdateIncomingRequest(
    @SpanAttribute("arg.Incoming") @NotNull Incoming Incoming
  ) {
    Log.debugf("Partially updating Incoming: %s", Incoming);
    return this.IncomingRepository.findById(Incoming.getId())
      .onItem()
      .ifNotNull()
      .transform(h -> {
        this.IncomingPartialUpdateMapper.mapPartialUpdate(Incoming, h);
        return h;
      })
      .onItem()
      .ifNotNull()
      .transform(this::validatePartialUpdate);
  }

  /**
   * Validates a {@link Incoming} for partial update according to annotation validation rules on the {@link Incoming} object.
   * @param Incoming The {@link Incoming}
   * @return The same {@link Incoming} that was passed in, assuming it passes validation. The return is used as a convenience so the method can be called in a functional pipeline.
   * @throws ConstraintViolationException If validation fails
   */
  private Incoming validatePartialUpdate(Incoming Incoming) {
    var violations = this.validator.validate(Incoming);

    if ((violations != null) && !violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    return Incoming;
  }


  @WithSpan("IncomingService.processIncomingRequest")
  public Uni<Outgoing> processIncomingRequest(@SpanAttribute("arg.incoming") @NotNull @Valid  Incoming incoming) {
  Log.debugf("process request: %s", incoming);
  return process(incoming)
  .chain(this::saveIncomingRequest)
  .replaceWith(incoming)
  .map(this.IncomingToMDBMapper::toSchema)
  .invoke(f -> this.emitter.send(Message.of(f, Metadata.of(TracingMetadata.withCurrent(Context.current())))))
 .replaceWith(incoming)
  .map(this.IncomingToMDBMapper::toOutgoing);
  }



  Uni<Incoming> process(@SpanAttribute("arg.incoming") Incoming incoming) {
  Log.debugf("Process incoming request and create a MDB request from: %s", incoming);
	  // Amazingly fancy logic to process request...
	  return Uni.createFrom().item(() -> {
		 
			  return incoming;
		  }
	  );
  }

 





}
