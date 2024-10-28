package org.osttra.user.service;

import lombok.RequiredArgsConstructor;
import org.osttra.exceptions.EmailAlreadyExistsException;
import org.osttra.exceptions.Messages;
import org.osttra.exceptions.UserNotFoundException;
import org.osttra.user.repository.model.User;
import org.osttra.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(Messages.USER_NOT_FOUND, id)));
    }

    public UUID saveUser(User user) {
        try {
            return userRepository.save(user).getId();
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyExistsException(String.format(Messages.EMAIL_ALREADY_EXISTS, user.getEmail()));
        }
    }
}
