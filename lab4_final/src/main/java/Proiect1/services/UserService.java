package Proiect1.services;

import Proiect1.dtos.UserDTO;
import Proiect1.dtos.UserLoginDTO;
import Proiect1.dtos.UserRegistrationDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    UserDTO registerUser(UserRegistrationDTO registrationDTO);
    UserDTO loginUser(UserLoginDTO loginDTO);
    UserDTO getUserByEmail(String email);


}
