
package com.rest.user_service.service;

import com.rest.user_service.exception.CustomResourceNotFoundException;
import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.model.AppRole;
import com.rest.user_service.model.Role;
import com.rest.user_service.model.User;
import com.rest.user_service.payload.UserDTO;
import com.rest.user_service.payload.UserRequestDTO;
import com.rest.user_service.repository.RoleRepository;
import com.rest.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
    public UserDTO getUserById(Long userId) {
        User user = findById(userId);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(UserDTO userDto, Long userId) {

        User user = findById(userId);
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());

        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    @Override
    public APIResponse deleteUser(Long userid) {
        findById(userid);
        userRepository.deleteById(userid);
        return new APIResponse("User with id " + userid + " deleted");
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomResourceNotFoundException("User", "user id", userId.toString()));
    }

}
