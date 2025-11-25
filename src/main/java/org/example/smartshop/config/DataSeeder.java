package org.example.smartshop.config;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entity.Client;
import org.example.smartshop.entity.User;
import org.example.smartshop.enums.CustomerTier;
import org.example.smartshop.enums.UserRole;
import org.example.smartshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            
            // Admin user
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .build();
            userRepository.save(admin);
            
            // Client user
            User clientUser = User.builder()
                .username("client")
                .password(passwordEncoder.encode("client123"))
                .role(UserRole.CLIENT)
                .build();
            
            Client client = Client.builder()
                .nom("Hassan Alami")
                .email("hassan@test.com")
                .niveauFidelite(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .user(clientUser)
                .build();
            
            clientUser.setClient(client);
            userRepository.save(clientUser);
            
            System.out.println("✅ Seeders créés:");
            System.out.println("   Admin - username: admin, password: admin123");
            System.out.println("   Client - username: client, password: client123");
        }
    }
}
