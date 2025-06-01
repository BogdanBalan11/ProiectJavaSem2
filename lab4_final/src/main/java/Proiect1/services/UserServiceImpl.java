package Proiect1.services;

import Proiect1.domain.User;
import Proiect1.dtos.UserDTO;
import Proiect1.dtos.UserLoginDTO;
import Proiect1.dtos.UserRegistrationDTO;
import Proiect1.exceptions.UserWithSameEmailExists;
import Proiect1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        logger.debug("Checking if email already exists: {}", registrationDTO.getEmail());

        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            logger.warn("Registration failed - user already exists: {}", registrationDTO.getEmail());
            throw new UserWithSameEmailExists();
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setName(registrationDTO.getName());
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);
        logger.info("User registered: {}", user.getEmail());
        return convertToDTO(user);
    }

    @Override
    public UserDTO loginUser(UserLoginDTO loginDTO) {
        logger.debug("Login attempt for email: {}", loginDTO.getEmail());
        Optional<User> userOpt = userRepository.findByEmail(loginDTO.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                logger.info("Login successful for user: {}", user.getEmail());
                return convertToDTO(user);
            }
        }
        logger.warn("Login failed for email: {}", loginDTO.getEmail());
        throw new BadCredentialsException("Invalid email or password");
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setBalance(user.getBalance());
        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by username (email): {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found during authentication: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
        return convertToDTO(user);
    }
}
