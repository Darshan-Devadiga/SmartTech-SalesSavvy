package com.dd.SalesSavvy.customer.customerServiceImplementations;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.Entities.JWTToken;
import com.dd.SalesSavvy.customer.customerRepositories.CustomerRepository;
import com.dd.SalesSavvy.customer.customerRepositories.JWTTokenRepo;
import com.dd.SalesSavvy.customer.customerServices.AuthServiceContract;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService implements AuthServiceContract {

    private final Key SIGNING_KEY;

    CustomerRepository customerRepository;
    JWTTokenRepo jwtTokenRepo;
    BCryptPasswordEncoder encoder;

    public AuthService(CustomerRepository customerRepository, JWTTokenRepo jwtTokenRepo, @Value("${jwt.secret}") String jwtSecret) {
        this.customerRepository = customerRepository;
        this.jwtTokenRepo = jwtTokenRepo;
        this.encoder = new BCryptPasswordEncoder();

        if(jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
            throw new IllegalArgumentException("JWT_SECRET in application.properties must be at least 64 bytes long for HS512.");
        }
        this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Customer authenticate(String username, String password) {
        Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Invalid username"));

        if(!encoder.matches(password, customer.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }
        return customer;
    }

    @Override
    public String generateToken(Customer customer) {
        String token;
        LocalDateTime now = LocalDateTime.now();
        JWTToken existingToken = jwtTokenRepo.findByUserId(customer.getUser_id());

        if(existingToken!=null && now.isBefore(existingToken.getExpiresAt())) {
            token = existingToken.getToken();
        } else {
            token = generateNewToken(customer);
            if(existingToken != null) {
                jwtTokenRepo.delete(existingToken);
            }
            saveToken(customer, token);
        }
        return token;
    }

    public String generateNewToken(Customer customer) {
        return Jwts.builder()
                .setSubject(customer.getUsername())
                .claim("role",customer.getRole().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+3600000))
                .signWith(SIGNING_KEY)
                .compact();
    }

    public void saveToken(Customer customer, String token) {
        JWTToken jwtToken = new JWTToken(customer, token, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        jwtTokenRepo.save(jwtToken);
    }

    @Override
    public boolean validateToken(String token) {
        try{
            System.err.println("VALIDATING TOKEN");

            //Parse and validate the token
            Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token);

            // Check if token exist on database and is not expired
            Optional<JWTToken> jwtToken = jwtTokenRepo.findByToken(token);
            if(jwtToken.isPresent()) {
                System.err.println("Token Expiry: " + jwtToken.get().getExpiresAt());
                System.err.println("Current Time: " + LocalDateTime.now());
                return jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
            }
            return false;
        } catch (Exception e) {
            System.err.println("Token Validation failed: " + e.getMessage());
            return  false;
        }
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public void logout (Customer user) {
        int userId = user.getUser_id();
        // Retrieve the JWT token associated with the user
        JWTToken token = jwtTokenRepo.findByUserId(userId);
        // If a token exists, delete it from the repository
        if (token != null) {
            jwtTokenRepo.deleteByUserId(userId);
        }
    }
}
