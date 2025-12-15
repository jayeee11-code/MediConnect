package com.MediConnect.services;

import com.MediConnect.dto.auth.*;
import com.MediConnect.exceptions.ValidationException;
import com.MediConnect.models.User;
import com.MediConnect.models.Patient;
import com.MediConnect.models.Doctor;
import com.MediConnect.repositories.*;
import com.MediConnect.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate unique constraints
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ValidationException("Phone number already registered");
        }

        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(User.UserRole.valueOf(request.getRole().toUpperCase()));
        user.setActive(true);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        // Create role-specific profile
        String profileId = null;
        if (savedUser.getRole() == User.UserRole.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            Patient savedPatient = patientRepository.save(patient);
            profileId = savedPatient.getId();
        } else if (savedUser.getRole() == User.UserRole.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUser(savedUser);
            Doctor savedDoctor = doctorRepository.save(doctor);
            profileId = savedDoctor.getId();
        }

        // Generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole().name())
                .profileId(profileId)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("User not found"));

        String profileId = null;
        if (user.getRole() == User.UserRole.PATIENT) {
            profileId = patientRepository.findByUserId(user.getId())
                    .map(Patient::getId)
                    .orElse(null);
        } else if (user.getRole() == User.UserRole.DOCTOR) {
            profileId = doctorRepository.findByUserId(user.getId())
                    .map(Doctor::getId)
                    .orElse(null);
        }

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .profileId(profileId)
                .build();
    }
}