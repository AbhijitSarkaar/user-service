
package com.rest.user_service.service;

import com.rest.user_service.exception.CustomResourceNotFoundException;
import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.model.AppRole;
import com.rest.user_service.model.Role;
import com.rest.user_service.model.User;
import com.rest.user_service.payload.UserDTO;
import com.rest.user_service.payload.UserDetailsDTO;
import com.rest.user_service.payload.UserRequestDTO;
import com.rest.user_service.repository.RoleRepository;
import com.rest.user_service.repository.UserRepository;
import com.rest.user_service.security.jwt.JwtUtils;
import com.rest.user_service.security.payload.LogInRequestDTO;
import com.rest.user_service.security.service.UserDetailsImpl;
import com.rest.user_service.security.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(item -> modelMapper.map(item, UserDTO.class))
                .toList();
    }

    @Override
    public UserDTO createUser(UserRequestDTO userRequestDto) {

        if(userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username  already exists");
        }

        if(userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email  already exists");
        }

        User user = new User(
                userRequestDto.getUsername(),
                passwordEncoder.encode(userRequestDto.getPassword()),
                userRequestDto.getEmail()
        );

        List<String> roleList = userRequestDto.getRoles();
        if(roleList == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role user does not exist"));
            user.setRole(userRole); // creates a db entry in user_role join table
        } else {
            roleList.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role admin does not exist"));
                        user.setRole(adminRole);
                        break;

                    case "user":
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role user does not exist"));
                        user.setRole(userRole);
                        break;
                }
            });
        }
        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    @Override
    public UserDetailsDTO getUserDetails() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        UserDetailsDTO userDetailsDto = new UserDetailsDTO();

        userDetailsDto.setId(userDetails.getId());
        userDetailsDto.setUsername(userDetails.getUsername());
        userDetailsDto.setEmail(userDetails.getEmail());

        List<String> roles = new ArrayList<>();

        userDetails.getAuthorities().forEach(item -> {
            roles.add(item.getAuthority());
        });

        userDetailsDto.setRoles(roles);

        return userDetailsDto;
    }

    @Override
    public UserDTO updateUser(UserDTO userDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = findById(userDetails.getId());
        if(userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if(userDto.getUsername() != null) user.setUsername(userDto.getUsername());

        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    @Override
    public APIResponse deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        findById(userDetails.getId());
        userRepository.deleteById(userDetails.getId());
        return new APIResponse("User with id " + userDetails.getId() + " deleted");
    }

    @Override
    public ResponseCookie userLogin(LogInRequestDTO logInRequestDto) {

        Authentication authentication;

        try {

            // get user details from custom userDetailsService implementation
            UserDetails userDetails = userDetailsService.loadUserByUsername(logInRequestDto.getUsername());
            // get authentication object from UsernamePasswordAuthenticationToken class
            authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            // set authentication object in security context holder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // get jwt token and set it to response cookie

            return jwtUtils.generateJwtCookie(userDetails.getUsername());

        } catch(RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomResourceNotFoundException("User", "user id", userId.toString()));
    }

}
