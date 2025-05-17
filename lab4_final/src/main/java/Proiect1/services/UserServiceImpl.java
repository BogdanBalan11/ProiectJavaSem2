package Proiect1.services;

import Proiect1.domain.User;
import Proiect1.dtos.UserDTO;
import Proiect1.dtos.UserLoginDTO;
import Proiect1.dtos.UserRegistrationDTO;
import Proiect1.exceptions.UserWithSameEmailExists;
import Proiect1.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceImpl implements  UserService{

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        boolean userWithSameEmailExists = userRepository.existsByEmail(registrationDTO.getEmail());

        if (userWithSameEmailExists) {
            throw new UserWithSameEmailExists();
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword());
        user.setName(registrationDTO.getName());
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    public UserDTO loginUser(UserLoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByEmail(loginDTO.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (loginDTO.getPassword().equals(user.getPassword())) {
                return convertToDTO(user);
            }
        }
        return null;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setBalance(user.getBalance());
        return dto;
    }
}
