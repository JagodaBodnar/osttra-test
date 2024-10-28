package org.osttra.unittests.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.osttra.exceptions.EmailAlreadyExistsException;
import org.osttra.exceptions.Messages;
import org.osttra.message.service.MessageFacade;
import org.osttra.user.controller.UserController;
import org.osttra.user.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MessageFacade messageFacade;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("When post method createUser should save user and return user's uuid.")
    public void shouldSaveUserAndReturnUUID_whenPostCreateUser() throws Exception {
        /*ARRANGE*/
        UserRequestDto userRequestDto = new UserRequestDto("Henrik",
                "Eriksson",
                "henrik.eriksson@gmail.com");
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);
        UUID expectedUUID = UUID.fromString("042484f0-be2f-49b1-a741-6962ef991718");
        when(messageFacade.createUser(any(UserRequestDto.class)))
                .thenReturn(expectedUUID);

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(expectedUUID.toString()));
    }

    @Test
    @DisplayName("When post method createUser and email repeated should return 400 and error message.")
    public void shouldReturn400AndMessage_whenPostCreateUserWithRepeatedEmail() throws Exception {
        /*ARRANGE*/
        UserRequestDto userRequestDto = new UserRequestDto("Henrik",
                "Eriksson",
                "henrik.eriksson@gmail.com");
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);
        when(messageFacade.createUser(userRequestDto))
                .thenThrow(new EmailAlreadyExistsException(
                        String.format(Messages.EMAIL_ALREADY_EXISTS,"henrik.eriksson@gmail.com")));

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(String.format(Messages.EMAIL_ALREADY_EXISTS,"henrik.eriksson@gmail.com")));
    }

    @Test
    @DisplayName("When post body has blank firstName should return 400 and error message.")
    public void shouldReturn400Status_whenPostCreateUserWithBlankFirstName() throws Exception {
        /*ARRANGE*/
        UserRequestDto userRequestDto = new UserRequestDto("",
                "Eriksson",
                "henrik.eriksson@gmail.com");
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.FIRST_NAME_MUST_NOT_BE_BLANK));
    }

    @Test
    @DisplayName("When post body has blank blank lastName should return 400 and error message.")
    public void shouldReturn400Status_whenPostCreateUserWithBlankLastName() throws Exception {
        /*ARRANGE*/
        UserRequestDto userRequestDto = new UserRequestDto("Henrik",
                "",
                "henrik.eriksson@gmail.com");
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.LAST_NAME_MUST_NOT_BE_BLANK));
    }

    @Test
    @DisplayName("When post body has blank email should return 400 and error message.")
    public void shouldReturn400Status_whenPostCreateUserWithBlankEmail() throws Exception {
        /*ARRANGE*/
        UserRequestDto userRequestDto = new UserRequestDto("Henrik",
                "Eriksson",
                "");
        String jsonRequest = objectMapper.writeValueAsString(userRequestDto);

        /*ACT AND ASSERT*/
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.EMAIL_MUST_NOT_BE_BLANK));
    }
}
