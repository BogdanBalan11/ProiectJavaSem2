package Proiect1.exceptions;

public class UserWithSameEmailExists extends RuntimeException {

    public UserWithSameEmailExists() {
        super("A User with the same email already exists");
    }
}