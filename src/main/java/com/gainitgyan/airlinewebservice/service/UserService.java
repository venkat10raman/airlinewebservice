package com.gainitgyan.airlinewebservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Positive;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gainitgyan.airlinewebservice.dto.AuthorityDto;
import com.gainitgyan.airlinewebservice.dto.RoleDto;
import com.gainitgyan.airlinewebservice.dto.UserDto;
import com.gainitgyan.airlinewebservice.entity.Authority;
import com.gainitgyan.airlinewebservice.entity.Role;
import com.gainitgyan.airlinewebservice.entity.User;
import com.gainitgyan.airlinewebservice.repository.IRoleRepository;
import com.gainitgyan.airlinewebservice.repository.IUserRepository;
import com.gainitgyan.airlinewebservice.security.UserPrincipal;

@Service
@Qualifier("UserDetailsService")
public class UserService implements IUserService {
	
	@Autowired
	IUserRepository userRepo;
	
	@Autowired
	IRoleRepository roleRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		User user = userRepo.findUserByUserName(userName);
		
		if(user == null) {
			System.out.println("User Not Found "+userName);
			throw new UsernameNotFoundException("User Not Found "+ userName);
		}
		UserDto dto = this.getUserDto(user);
		UserPrincipal principal = new UserPrincipal(dto);
		return principal;
	}
	
	

	@Override
	public UserDto createUser(UserDto userDto) {
		
		if(checkIfUserNameExists(userDto)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "User Name already Exists");
		}
		if(checkIfUserEmailIdExists(userDto)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "User Email Id  already Exists");
		}
		User user = new User();
		
		BeanUtils.copyProperties(userDto, user);
		
		user.setEmailId(user.getEmailId().toLowerCase());
		user.setUserName(user.getUserName().toLowerCase());
		
		Role role = roleRepo.findByName("ROLE_ENQUIRY");
		
		Set<Role> roles  = new HashSet<>();
		roles.add(role);
		
		user.setRoles(roles);
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user = userRepo.save(user);
		
		userDto = this.getUserDto(user);
		return userDto;
	}
	
	private UserDto getUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setEmailId(user.getEmailId());
		userDto.setEnabled(user.isEnabled());
		userDto.setFirstName(user.getFirstName());
		userDto.setId(user.getId());
		userDto.setLastName(user.getLastName());
		userDto.setUserName(user.getUserName());
		userDto.setPassword(user.getPassword());
		
		Set<RoleDto> rSet = new HashSet<>();
		for(Role r : user.getRoles()) {
			RoleDto rDto = new RoleDto();
			rDto.setName(r.getName());
			Set<AuthorityDto> aSet = new HashSet<>();
			
			for(Authority a : r.getAuthorities()) {
				AuthorityDto aDto = new AuthorityDto(a.getName());
				aSet.add(aDto);
			}
			rDto.setAuthorities(aSet);
			rSet.add(rDto);
		}
		userDto.setRoles(rSet);
		
		return userDto;
	}

	
	public boolean checkIfUserNameExists(UserDto userDto) {
		return StringUtils.isNotBlank(userDto.getUserName()) && userRepo.findUserByUserName(userDto.getUserName().toLowerCase()) !=null;
	}
	private boolean checkIfUserEmailIdExists(UserDto userDto) {
		return StringUtils.isNotBlank(userDto.getEmailId()) && userRepo.findUserByEmailId(userDto.getEmailId().toLowerCase()) !=null;
	}

	@Override
	public UserDto updateUser(UserDto userDto) {
		
//		User user = userRepo.findUserByUserName(userDto.getUserName().toLowerCase());
		
		User user = userRepo.findById(userDto.getId())
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found."));
		
		
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		
		Set<Role> rSet = new HashSet<>();
		for(RoleDto rDto : userDto.getRoles()) {
			rSet.add(roleRepo.findByName(rDto.getName()));
		}
		user.setRoles(rSet);
		user = userRepo.save(user);
		userDto.setId(user.getId());
		
		return userDto;
	}
	@Override
	public UserDto findUserByUserName(String userName) {
		return this.getUserDto(this.userRepo.findUserByUserName(userName));
	}



	@Override
	public List<UserDto> getAllUserList() {
		List<User> userList = userRepo.findAll();
		List<UserDto> userDtoList = userList.stream().map(user -> {
			UserDto userDto = getUserDto(user);
			userDto.setPassword(null);
			return userDto;
		}).collect(Collectors.toList());
		return userDtoList;
	}



	@Override
	public UserDto getUserById(@Positive Integer userId) {
		UserDto userDto = null;
		Optional<User> optional = userRepo.findById(userId);
		if(optional.get() != null) {
			userDto = getUserDto(optional.get());
		}
		return userDto;
	}



	@Override
	public void deleteUser(@Positive Integer userId) {
		userRepo.findById(userId)
			.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found."));
		userRepo.deleteById(userId);
	}

	
}
