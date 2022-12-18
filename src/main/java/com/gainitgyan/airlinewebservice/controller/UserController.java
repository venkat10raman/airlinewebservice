package com.gainitgyan.airlinewebservice.controller;

import java.util.List;

import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gainitgyan.airlinewebservice.dto.UserDto;
import com.gainitgyan.airlinewebservice.security.UserPrincipal;
import com.gainitgyan.airlinewebservice.service.IUserService;
import com.gainitgyan.airlinewebservice.util.JWTTokenUtil;

@RestController
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTTokenUtil jwtTokenUtil;
	
	@PostMapping(path = {"/register-user"}, consumes = {MediaType.APPLICATION_JSON_VALUE} , produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
		userDto = userService.createUser(userDto);
		return ResponseEntity.ok().body(userDto);
	}
	
	@PutMapping(path = {"/update-user"}, consumes = {MediaType.APPLICATION_JSON_VALUE} , produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
		userDto = userService.updateUser(userDto);
		return ResponseEntity.ok().body(userDto);
	}
	
	@GetMapping(path = {"/user"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('user_read')")
	public ResponseEntity<List<UserDto>> getUserList() {
		List<UserDto> userDtoList = userService.getAllUserList();
		return ResponseEntity.ok().body(userDtoList);
		
	}
	
	@GetMapping(path = {"/user/{id}"} , produces = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('user_read')")
	public ResponseEntity<UserDto> getUser(@PathVariable(name = "id") @Positive Integer userId)  {
		UserDto userdto = userService.getUserById(userId);
		return ResponseEntity.ok().body(userdto);
	}
	
	@DeleteMapping(path = "/user/delete/{id}")
	@PreAuthorize("hasAuthority('user_delete')")
	public void deleteUser(@PathVariable(name = "id") @Positive Integer userId) {
		userService.deleteUser(userId);
	}
	
	@PostMapping(path="/login" , consumes = {MediaType.APPLICATION_JSON_VALUE} , produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
		
		UserPrincipal userPrinciple = this.authenticate(userDto.getUserName(), userDto.getPassword());
//		UserDto user = this.userService.findUserByUserName(userDto.getUserName());
//		user.setPassword(null);
		userPrinciple.getUserDto().setPassword(null);
		
		String jwtToken = this.jwtTokenUtil.generateToken(userPrinciple);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+jwtToken);
		
		return ResponseEntity.ok().headers(headers).body(userPrinciple.getUserDto());
		
	}
	
	private UserPrincipal authenticate(String userName, String password) {
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
		return (UserPrincipal) authenticate.getPrincipal();
	}
}
