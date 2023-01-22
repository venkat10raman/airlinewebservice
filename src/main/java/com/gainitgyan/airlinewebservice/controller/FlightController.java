package com.gainitgyan.airlinewebservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gainitgyan.airlinewebservice.dto.FlightDto;
import com.gainitgyan.airlinewebservice.exception.ApiErrorResponse;
import com.gainitgyan.airlinewebservice.service.IFlightService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@Tag(name = "Flight" , description = "Flight Resource")
public class FlightController {

	
	@Autowired
	private IFlightService flightService;

	// Http GET method - Read operation
	
	@Operation(summary = "Get Flight", description = "Fetch the Flight record by flight id" , method = "Get", tags = {"Flight"})
	@Parameter(name = "id" , example = "8" , description = "Flight id" , in = ParameterIn.PATH)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200" , description = "Fetch is Successful",
					content = @Content(schema = @Schema(implementation = FlightDto.class),
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "404" , description = "Flight Resource Not Found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode="500" , description = "Server Exception",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE))
	})
	@GetMapping(path = "/flight/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('flight_read')")
	public EntityModel<FlightDto> getFlight(@PathVariable(name = "id") @Positive Integer flightId) {
		
		Link link = linkTo(methodOn(FlightController.class).getFlight(flightId)).withSelfRel();
		FlightDto dto = flightService.getFlight(flightId);
		dto.add(link);
		
		return EntityModel.of(dto);
	}

	@Operation(summary = "Get All Flights" , description = "Fetch All the Flight records" , method = "GET" , tags= {"Flight"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200" , description = "Fetch is Successful",
					content = @Content(schema = @Schema(implementation = FlightDto.class),
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "404" , description = "Flight Resource Not Found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode="500" , description = "Server Exception",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE))
	})
	@GetMapping(path = "/flight")
	@PreAuthorize("hasAuthority('flight_read')")
	public CollectionModel<FlightDto> getAllFlights() {
		List<FlightDto> list = flightService.getAllFlights();
		for(FlightDto dto : list) {
			Link link = linkTo(methodOn(FlightController.class).getFlight(dto.getId())).withSelfRel();
			dto.add(link);
		}
		
		Link link = linkTo(methodOn(FlightController.class , getAllFlights())).withSelfRel();
		
		return CollectionModel.of(list, link);
	}

	@Operation(summary = "Get Flight" , description = "Fetch the Flight record by flight number" , method = "GET" , tags= {"Flight"})
	@Parameter(name = "flightNumber",  example = "22" ,  required = true ,  description = "Flight Number",in = ParameterIn.PATH)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200" , description = "Fetch is Successful",
					content = @Content(schema = @Schema(implementation = FlightDto.class),
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "404" , description = "Flight Resource Not Found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode="500" , description = "Server Exception",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE))
	})
	@GetMapping(path = "/flight/flightData/{flightNumber}")
	@PreAuthorize("hasAuthority('flight_read')")
	public ResponseEntity<FlightDto> getFlightByFlightNumber(@PathVariable(name = "flightNumber") String flightNumber) {
		FlightDto dto = flightService.getFlightByflightNumber(flightNumber);
		
		return ResponseEntity.ok().body(dto);
	}

	@Operation(summary = "Create Flight" , description = "Create the Flight record" , method = "POST" , tags= {"Flight"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200" , description = "Flight Record Successfully Created",
					content = @Content(schema = @Schema(implementation = FlightDto.class),
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode="500" , description = "Server Exception",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE))
	})
	@PostMapping(path = "/flight",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('flight_create')")
	public ResponseEntity<FlightDto> createFlight(@RequestBody @Valid FlightDto flightDto) {
		FlightDto dto = flightService.createFlight(flightDto);
		return ResponseEntity.ok().body(dto);
	}

	@Operation(summary = "Update Flight" , description = "Update the Flight record" , method = "PUT" , tags= {"Flight"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200" , description = "Flight Record Successfully Created",
					content = @Content(schema = @Schema(implementation = FlightDto.class),
					mediaType = MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = "404" , description = "Flight Resource Not Found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode="500" , description = "Server Exception",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE))
	})
	@PutMapping(path = "/flight",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('flight_update')")
	public ResponseEntity<FlightDto> updateFlight(@RequestBody @Valid FlightDto flightDto) {
		FlightDto dto = flightService.updateFlight(flightDto);
		return ResponseEntity.ok().body(dto);
	}

	@Operation(summary = "Delete Flight" , description = "Delete the Flight record" , method = "DELETE" , tags= {"Flight"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200" , description = "Flight Record Successfully Created",
					content = @Content()),
			@ApiResponse(responseCode = "404" , description = "Flight Resource Not Found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE)),
			
			@ApiResponse(responseCode="500" , description = "Server Exception",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
					mediaType=MediaType.APPLICATION_JSON_VALUE))
	})
	@DeleteMapping(path = "/flight/delete/{id}")
	@PreAuthorize("hasAuthority('flight_delete')")
	public void deleteFlight(@PathVariable(name = "id") @Positive Integer flightId) {
		flightService.deleteFlight(flightId);
	}

}
