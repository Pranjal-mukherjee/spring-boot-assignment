package com.zemoso.springboot.assignment.service;

import com.zemoso.springboot.assignment.dto.UserDTO;
import com.zemoso.springboot.assignment.entity.Book;
import com.zemoso.springboot.assignment.entity.User;
import com.zemoso.springboot.assignment.repository.BookRepository;
import com.zemoso.springboot.assignment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private BookRepository bookRepository;
    String userNotFound = "User not found with id";

    public UserService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(userNotFound + id));

        return convertToDto(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        Book book = bookRepository.findById(userDTO.getBookId())
                .orElseThrow(() ->
                        new NoSuchElementException(("Book not found with id " +
                                userDTO.getBookId())));

        User user = convertToEntity(userDTO);
        user.setBook(book);

        // Clear the ID to ensure it is generated by the database
        user.setId(null);

        user = userRepository.save(user);
       
        return convertToDto(user);
    }

    public UserDTO updateUser(UserDTO userDTO) {
        User existingUser = userRepository.findById(userDTO.getId())
                .orElseThrow(()
                -> new NoSuchElementException(userNotFound + userDTO.getId()));

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        Book book = bookRepository.findById(userDTO.getBookId())
                .orElseThrow(()
                        -> new NoSuchElementException(userNotFound + userDTO.getBookId()));
        existingUser.setBook(book);

        User user = userRepository.save(existingUser);
        return convertToDto(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        Book book = user.getBook();
        if (book != null) {
            userDTO.setBookId(user.getBook().getId());
        }
        else {
            userDTO.setBookId(null);
        }
        userDTO.setEmail(user.getEmail());


        return userDTO;
    }
    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
