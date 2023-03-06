package com.jkeya.workshop.rest;
import java.util.List;
import static javax.ws.rs.core.MediaType.*;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.jkeya.workshop.Incoming;
import com.jkeya.workshop.Outgoing;
import com.jkeya.workshop.service.IncomingService;

import io.quarkus.logging.Log;


import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;

/**
 * JAX-RS API endpoints with {@code /api/fights} as the base URI for all endpoints
 */
@Path("/api/esb")
@Produces(APPLICATION_JSON)
@Tag(name = "esb-servlet")
public class IncomingResource {

	private final IncomingService service;

	public IncomingResource(IncomingService service) {
		this.service = service;
	}

    @POST
	@Consumes(APPLICATION_JSON)
	@Operation(summary = "Initiates a request")
	@APIResponse(
		responseCode = "200",
		description = "The Request"
	)
	@APIResponse(
		responseCode = "400",
		description = "Invalid Request passed in (or no request body found)"
	)
	public Uni<Outgoing> perform(@NotNull @Valid Incoming incoming) {
		Log.debugf("Incomings request: %s", incoming);
		
		return this.service.processIncomingRequest(incoming);
	}

}