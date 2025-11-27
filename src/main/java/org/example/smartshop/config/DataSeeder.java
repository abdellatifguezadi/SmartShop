package org.example.smartshop.config;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entity.Client;
import org.example.smartshop.entity.Product;
import org.example.smartshop.entity.PromoCode;
import org.example.smartshop.entity.User;
import org.example.smartshop.enums.CustomerTier;
import org.example.smartshop.enums.UserRole;
import org.example.smartshop.repository.ProductRepository;
import org.example.smartshop.repository.PromoCodeRepository;
import org.example.smartshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;
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
            
            // Client BASIC
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
            
            // Client SILVER avec historique
            User silverUser = User.builder()
                .username("silver")
                .password(passwordEncoder.encode("silver123"))
                .role(UserRole.CLIENT)
                .build();

            Client silverClient = Client.builder()
                .nom("Fatima Zahra")
                .email("fatima@test.com")
                .niveauFidelite(CustomerTier.SILVER)
                .totalOrders(3)
                .totalSpent(new BigDecimal("600.00"))
                .user(silverUser)
                .build();

            silverUser.setClient(silverClient);
            userRepository.save(silverUser);

            // Client GOLD avec historique
            User goldUser = User.builder()
                .username("gold")
                .password(passwordEncoder.encode("gold123"))
                .role(UserRole.CLIENT)
                .build();

            Client goldClient = Client.builder()
                .nom("Ahmed Benani")
                .email("ahmed@test.com")
                .niveauFidelite(CustomerTier.GOLD)
                .totalOrders(8)
                .totalSpent(new BigDecimal("7500.00"))
                .user(goldUser)
                .build();

            goldUser.setClient(goldClient);
            userRepository.save(goldUser);

            // Client PLATINUM avec historique
            User platinumUser = User.builder()
                .username("platinum")
                .password(passwordEncoder.encode("platinum123"))
                .role(UserRole.CLIENT)
                .build();

            Client platinumClient = Client.builder()
                .nom("Salma Idrissi")
                .email("salma@test.com")
                .niveauFidelite(CustomerTier.PLATINUM)
                .totalOrders(15)
                .totalSpent(new BigDecimal("18000.00"))
                .user(platinumUser)
                .build();

            platinumUser.setClient(platinumClient);
            userRepository.save(platinumUser);

            System.out.println("✅ Seeders créés:");
            System.out.println("   Admin - username: admin, password: admin123");
            System.out.println("   Client BASIC - username: client, password: client123");
            System.out.println("   Client SILVER - username: silver, password: silver123 (3 orders, 2500 DH)");
            System.out.println("   Client GOLD - username: gold, password: gold123 (8 orders, 7500 DH)");
            System.out.println("   Client PLATINUM - username: platinum, password: platinum123 (15 orders, 18000 DH)");
        }

        // Seed Products
        if (productRepository.count() == 0) {
            Product laptop = Product.builder()
                .nom("Laptop Dell XPS 15")
                .description("Laptop puissant pour développeurs")
                .prixUnitaire(new BigDecimal("12000.00"))
                .stockDisponible(10)
                .deleted(false)
                .build();
            productRepository.save(laptop);

            Product smartphone = Product.builder()
                .nom("iPhone 15 Pro")
                .description("Dernier smartphone Apple")
                .prixUnitaire(new BigDecimal("15000.00"))
                .stockDisponible(15)
                .deleted(false)
                .build();
            productRepository.save(smartphone);

            Product headphones = Product.builder()
                .nom("Sony WH-1000XM5")
                .description("Casque à réduction de bruit")
                .prixUnitaire(new BigDecimal("3500.00"))
                .stockDisponible(25)
                .deleted(false)
                .build();
            productRepository.save(headphones);

            Product keyboard = Product.builder()
                .nom("Keychron K8 Pro")
                .description("Clavier mécanique sans fil")
                .prixUnitaire(new BigDecimal("1200.00"))
                .stockDisponible(30)
                .deleted(false)
                .build();
            productRepository.save(keyboard);

            Product monitor = Product.builder()
                .nom("LG UltraWide 34\"")
                .description("Écran ultra-large 34 pouces")
                .prixUnitaire(new BigDecimal("4500.00"))
                .stockDisponible(12)
                .deleted(false)
                .build();
            productRepository.save(monitor);

            Product mouse = Product.builder()
                .nom("Logitech MX Master 3S")
                .description("Souris ergonomique professionnelle")
                .prixUnitaire(new BigDecimal("800.00"))
                .stockDisponible(40)
                .deleted(false)
                .build();
            productRepository.save(mouse);

            System.out.println("✅ Produits créés: 6 produits ajoutés");
        }

        // Seed Promo Codes
        if (promoCodeRepository.count() == 0) {
            PromoCode promo1 = PromoCode.builder()
                .code("PROMO-2024")
                .discountPercentage(new BigDecimal("0.05"))
                .used(false)
                .build();
            promoCodeRepository.save(promo1);

            PromoCode promo2 = PromoCode.builder()
                .code("PROMO-NOEL")
                .discountPercentage(new BigDecimal("0.05"))
                .used(false)
                .build();
            promoCodeRepository.save(promo2);

            PromoCode promo3 = PromoCode.builder()
                .code("PROMO-VIP1")
                .discountPercentage(new BigDecimal("0.05"))
                .used(false)
                .build();
            promoCodeRepository.save(promo3);

            PromoCode promo4 = PromoCode.builder()
                .code("PROMO-TEST")
                .discountPercentage(new BigDecimal("0.05"))
                .used(false)
                .build();
            promoCodeRepository.save(promo4);

            PromoCode promoUsed = PromoCode.builder()
                .code("PROMO-OLD1")
                .discountPercentage(new BigDecimal("0.05"))
                .used(true)
                .build();
            promoCodeRepository.save(promoUsed);

            System.out.println("✅ Codes promo créés: PROMO-2024, PROMO-NOEL, PROMO-VIP1, PROMO-TEST (5% chacun), PROMO-OLD1 (déjà utilisé)");
        }
    }
}
