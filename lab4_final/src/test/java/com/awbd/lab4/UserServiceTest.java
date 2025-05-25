package com.awbd.lab4;

import Proiect1.domain.User;
import Proiect1.dtos.UserDTO;
import Proiect1.dtos.UserLoginDTO;
import Proiect1.dtos.UserRegistrationDTO;
import Proiect1.exceptions.UserWithSameEmailExists;
import Proiect1.repositories.UserRepository;
import Proiect1.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("1234");
        dto.setName("John");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDTO result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("duplicate@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(UserWithSameEmailExists.class, () -> userService.registerUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoginUser_Success() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashed");
        user.setName("Test");
        user.setBalance(BigDecimal.valueOf(0));

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

        UserDTO result = userService.loginUser(dto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        UserLoginDTO dto = new UserLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("wrong");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashed");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.loginUser(dto));
    }

    @Test
    void testLoginUser_EmailNotFound() {
        when(userRepository.findByEmail("not@found.com")).thenReturn(Optional.empty());

        UserLoginDTO dto = new UserLoginDTO();
        dto.setEmail("not@found.com");
        dto.setPassword("any");

        assertThrows(BadCredentialsException.class, () -> userService.loginUser(dto));
    }

    @Test
    void testLoadUserByUsername_Success() {
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("encodedPass");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername(user.getEmail());

        assertEquals("admin@example.com", details.getUsername());
        assertEquals("encodedPass", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("missing@example.com"));
    }

    @Test
    void testGetUserByEmail_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@example.com");
        user.setName("Alice");
        user.setBalance(BigDecimal.valueOf(0));

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByEmail("email@example.com");

        assertEquals("Alice", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail("ghost@example.com"));
    }
}
