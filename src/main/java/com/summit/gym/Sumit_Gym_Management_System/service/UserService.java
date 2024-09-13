package com.summit.gym.Sumit_Gym_Management_System.service;

import com.summit.gym.Sumit_Gym_Management_System.dto.UserDto;
import com.summit.gym.Sumit_Gym_Management_System.enums.Role;
import com.summit.gym.Sumit_Gym_Management_System.exceptions.UserNotFoundException;
import com.summit.gym.Sumit_Gym_Management_System.model.User;
import com.summit.gym.Sumit_Gym_Management_System.reposiroty.UserRepo;
import com.summit.gym.Sumit_Gym_Management_System.validation.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private final UserValidator validator;

    private void encryptPassAndSaveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }


    public List<UserDto> findAllCashiers() {
        return userRepo.findByRole(Role.ROLE_CASHIER)
                .stream().map(
                        user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    public User findCashierByUserName(String userName) {
        return userRepo.findCashierByUserName(userName)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserDto findAdmin() {
        User admin = userRepo.findAdmin()
                .orElseThrow(UserNotFoundException::new);
        return modelMapper.map(admin, UserDto.class);
    }


    public UserDto findByUserName(String userName) {
        User user = userRepo.findByUserName(userName)
                .orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserDto.class);
    }


    public void saveUser(User user) {
//        if (userRepo.existsByUserName(user.getUsername())) {
//            throw new IllegalArgumentException("Username already taken");
//        }


        encryptPassAndSaveUser(user);
    }

    public void updateNormalUser(String oldUserName, User updatedUser) {
        if (!updatedUser.getUsername().equals(oldUserName)
                && userRepo.existsByUserName(updatedUser.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        User oldUser = userRepo.findByUserName(oldUserName)
                .orElseThrow(UserNotFoundException::new);
        if (oldUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalArgumentException("Unauthorized to update super user");
        }
        updatedUser.setId(oldUser.getId());
        updatedUser.setRole(oldUser.getRole());
        encryptPassAndSaveUser(updatedUser);
    }

    public void updateAdmin(User updatedAdmin) {
//        if (!updatedAdmin.getRole().equals(Role.ROLE_ADMIN)) {
//            throw new IllegalArgumentException("Admin account must have ROLE_ADMIN");
//        }

        User admin = userRepo.findAdmin()
                .orElseThrow(UserNotFoundException::new);

        if (!admin.getUsername().equals(updatedAdmin.getUsername())
                && userRepo.existsByUserName(updatedAdmin.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        updatedAdmin.setId(admin.getId());
        updatedAdmin.setRole(Role.ROLE_ADMIN);
        encryptPassAndSaveUser(updatedAdmin);
    }

    public void deleteCashier(String userName) {
        User userToDelete = findCashierByUserName(userName);
        userRepo.delete(userToDelete);
    }



}
