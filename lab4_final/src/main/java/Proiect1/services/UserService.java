package Proiect1.services;

import Proiect1.dtos.UserDTO;
import Proiect1.dtos.UserLoginDTO;
import Proiect1.dtos.UserRegistrationDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserDTO registerUser(UserRegistrationDTO registrationDTO);
    UserDTO loginUser(UserLoginDTO loginDTO);

}
