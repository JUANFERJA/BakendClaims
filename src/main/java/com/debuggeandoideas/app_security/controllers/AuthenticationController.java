package com.debuggeandoideas.app_security.controllers;

import com.debuggeandoideas.app_security.entites.CustomerEntity;
import com.debuggeandoideas.app_security.entites.JWTRequest;
import com.debuggeandoideas.app_security.entites.JWTResponse;
import com.debuggeandoideas.app_security.entites.ResponseMessage;
import com.debuggeandoideas.app_security.services.JWTService;
import com.debuggeandoideas.app_security.services.JWTUserDetailService;
import com.debuggeandoideas.app_security.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JWTUserDetailService jwtUserDetailService;
    private final JWTService jwtService;
    private final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> postToken(@RequestBody JWTRequest request) {
        CustomerEntity usuario = userService.getUser(request.getUsername());
        this.authenticate(request);
        final var userDetails = this.jwtUserDetailService.loadUserByUsername(request.getUsername());
        final var token = this.jwtService.generateToken(userDetails);

        if(usuario.getStatus().equals("1")){
            return ResponseEntity.ok(new JWTResponse(token, usuario.getId_user(),
                    usuario.getRoles().get(0).getId(), usuario.getFull_name(), usuario.getDni()));
        }else{
            return ResponseEntity.ok(new ResponseMessage("Usuario Bloqueado o Inactivo"));
        }

    }

    private void authenticate(JWTRequest request) {
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException | DisabledException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
