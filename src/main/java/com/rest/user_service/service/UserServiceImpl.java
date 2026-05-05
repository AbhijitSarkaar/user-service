
package com.rest.user_service.service;

import com.rest.user_service.exception.CustomResourceNotFoundException;
import com.rest.user_service.exception.response.APIResponse;
import com.rest.user_service.model.User;
import com.rest.user_service.payload.UserDTO;
import com.rest.user_service.payload.UserRequestDTO;
import com.rest.user_service.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(item -> modelMapper.map(item, UserDTO.class))
                .toList();
    }

    @Override
    public UserDTO createUser(UserRequestDTO userRequestDto) {
        User user = userRepository.save(modelMapper.map(userRequestDto, User.class));
        return modelMapper.map(user, UserDTO.class);
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
        userRepository.deleteById(userid);
        return new APIResponse("User with id " + userid + " deleted");
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomResourceNotFoundException("User", "user id", userId.toString()));
    }

}
