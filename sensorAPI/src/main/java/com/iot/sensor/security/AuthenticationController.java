package com.iot.sensor.security;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.iot.sensor.model.User;
import com.iot.sensor.model.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthenticationController {

	@Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    public AuthenticationController(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Endpoint unction to return a token based on user and password.
     * The user password is checked with the hashed password on the database.
     * 
     * @param authenticationRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
    	User user = userRepository.findByUsername(authenticationRequest.getUsername());
        
    	if (user != null && passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            final String token = jwtTokenUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        }
    }
    
    /**
     * Endpoint function to invalidate tokens (for the logout functionality)
     * It inserts a record to the invalidated tokens table
     * 
     * @param request
     * @return
     */
    @PostMapping("/invalidatetoken")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Date expirationDate = jwtTokenUtil.extractExpiration(token);
            long expirationTime = expirationDate.getTime() - System.currentTimeMillis();
            tokenBlacklistService.blacklistToken(token, expirationTime);
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }

    /**
     * Endpoint function used to validate if the token is still valid (used by the frontend before retrieving sensor data
     * 
     * @return
     */
    @PostMapping("/checktoken")
    public String getLastSensorData() {
        return null;
    }
}
