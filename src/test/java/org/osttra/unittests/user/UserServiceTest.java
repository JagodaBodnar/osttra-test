package org.osttra.unittests.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osttra.exceptions.UserNotFoundException;
import org.osttra.user.dto.UserRequestDto;
import org.osttra.user.repository.UserRepository;
import org.osttra.user.repository.model.User;
import org.osttra.user.service.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should throw UserNotFoundException when user with such id not found.")
    public void shouldThrowException_whenUserWithSuchIdNotFound() {
        /*ARRANGE*/
        UUID uuid = UUID.fromString("0bb5e4b4-507a-4fd6-b311-8e48336cfae1");
        when(userRepository.findById(uuid))
                .thenThrow(UserNotFoundException.class);
        /*ACT AND ASSERT*/
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(uuid));
    }

    @Test
    @DisplayName("Should return user for id that exists.")
    public void shouldReturnUser_whenUserIdExists() {
        /*ARRANGE*/
        UUID uuid = UUID.fromString("0bb5e4b4-507a-4fd6-b311-8e48336cfae1");
        when(userRepository.findById(uuid))
                .thenReturn(Optional.of(new User(uuid, "Henrik", "Eriksson", "henrik.nordin@gmail.com")));
        /*ACT*/
        User result = userService.getUserById(uuid);
        /*ASSERT*/
        assertAll("Grouped assertions for given userId",
                () -> assertEquals(result.getId(), uuid),
                () -> assertEquals(result.getEmail(), "henrik.nordin@gmail.com"));
    }

    @Test
    @DisplayName("Should create User from UserRequestDto.")
    public void shouldReturnUser_whenUserRequestDtoToUser() {
        /*ARRANGE*/
        UserRequestDto userRequestDto = new UserRequestDto("Henrik", "Eriksson", "henrik.nordin@gmail.com");
        /*ACT*/
        User expected = UserRequestDto.toUser(userRequestDto);
        /*ASSERT*/
        assertAll("Grouped mapping of UserRequestDto to User.",
                () -> assertEquals(userRequestDto.email(), expected.getEmail()),
                () -> assertEquals(userRequestDto.firstName(), expected.getFirstName()),
                () -> assertEquals(userRequestDto.lastName(), expected.getLastName())
        );
    }
}
