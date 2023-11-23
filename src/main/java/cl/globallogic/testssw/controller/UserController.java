package cl.globallogic.testssw.controller;

import cl.globallogic.testssw.dto.UserDto;
import cl.globallogic.testssw.entity.Error;
import cl.globallogic.testssw.entity.User;
import cl.globallogic.testssw.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> postSignUp(
            @RequestBody UserDto request) {
        try {
            User user = userService.createUser(request);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(handleError(e, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> postLogin(
            @RequestParam String token) {
        try {
            User user = userService.findUser(token);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(handleError(e, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    private Map<String, List<Error>> handleError(Exception e, int codigo) {
        Error err = new Error();

        err.setTimestamp(new Date());
        err.setCodigo(codigo);
        err.setDetail(e.getMessage());

        return Collections.singletonMap("error", Collections.singletonList(err));
    }
}
