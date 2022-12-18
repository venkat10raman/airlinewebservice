package com.gainitgyan.airlinewebservice.service;

import java.util.List;

import javax.validation.constraints.Positive;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.gainitgyan.airlinewebservice.dto.UserDto;

@Service
public interface IUserService extends UserDetailsService{

	public UserDto createUser(UserDto userDto);
	public UserDto updateUser(UserDto userDto);
	public UserDto findUserByUserName(String userName);
	public List<UserDto> getAllUserList();
	public UserDto getUserById(@Positive Integer userId);
	public void deleteUser(@Positive Integer userId);
}
