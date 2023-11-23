package cl.globallogic.testssw.service;

import cl.globallogic.testssw.dto.UserDto;
import cl.globallogic.testssw.entity.User;
import cl.globallogic.testssw.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final static String emailRegex =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    private final static String passwordRegex =
            "^(?=(?:[^A-Z]*[A-Z]){1})(?!.*[A-Z].*[A-Z])(?=(?:\\D*\\d){2})(?!.*\\d.*\\d.*\\d)[a-zA-Z\\d]{8,12}$";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserDto request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());

        validateDuplicateUser(request.getEmail());

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreated(new Date());
        user.setLastLogin(user.getCreated());
        user.setToken(generateToken(user.getEmail()));
        user.setActive(true);
        user.setPhones(request.getPhones());

        userRepository.save(user);

        return user;
    }

    public User findUser(String token) {
        Optional<User> userByToken = userRepository.findByToken(token);

        if (userByToken.isPresent()) {
            userByToken.get().setLastLogin(new Date());
            userByToken.get().setToken(generateToken(userByToken.get().getEmail()));
            userRepository.save(userByToken.get());
        } else {
            throw new IllegalArgumentException("No se encuentra al usuario");
        }
        return userByToken.orElse(null);
    }

    private void validateDuplicateUser(String email) {
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            throw new IllegalArgumentException("No esta permitido duplicar usuarios");
        }
    }

    private void validateEmail(String email) {
        if (!email.matches(UserService.emailRegex)) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido");
        }
    }

    private void validatePassword(String password) {
        if (!password.matches(UserService.passwordRegex)) {
            throw new IllegalArgumentException("Formato de contraseña inválido");
        }
    }

    private String generateToken(String email) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(email)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
