package com.gainitgyan.airlinewebservice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gainitgyan.airlinewebservice.service.IFlightService;

@RestController
@Validated
public class FlightController {

	
	@Autowired
	private IFlightService flightService;

	// Http GET method - Read operation
	
	@GetMapping(path = "/flight/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('flight_read')")
	public ResponseEntity<FlightDto> getFlight(@PathVariable(name = "id") @Positive Integer flightId) {
		
		FlightDto dto = flightService.getFlight(flightId);
		
		return ResponseEntity.ok().body(dto);
	}

	@GetMapping(path = "/flight")
	@PreAuthorize("hasAuthority('flight_read')")
	public ResponseEntity<List<FlightDto>> getAllFlights() {
		List<FlightDto> list = flightService.getAllFlights();
		return ResponseEntity.ok().body(list);
	}

	@GetMapping(path = "/flight/flightData/{flightNumber}")
	@PreAuthorize("hasAuthority('flight_read')")
	public ResponseEntity<FlightDto> getFlightByFlightNumber(@PathVariable(name = "flightNumber") String flightNumber) {
		FlightDto dto = flightService.getFlightByflightNumber(flightNumber);
		
		return ResponseEntity.ok().body(dto);
	}

	@PostMapping(path = "/flight",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('flight_create')")
	public ResponseEntity<FlightDto> createFlight(@RequestBody @Valid FlightDto flightDto) {
		FlightDto dto = flightService.createFlight(flightDto);
		return ResponseEntity.ok().body(dto);
	}

	@PutMapping(path = "/flight",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('flight_update')")
	public ResponseEntity<FlightDto> updateFlight(@RequestBody @Valid FlightDto flightDto) {
		FlightDto dto = flightService.updateFlight(flightDto);
		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(path = "/flight/delete/{id}")
	@PreAuthorize("hasAuthority('flight_delete')")
	public void deleteFlight(@PathVariable(name = "id") @Positive Integer flightId) {
		flightService.deleteFlight(flightId);
	}

}
