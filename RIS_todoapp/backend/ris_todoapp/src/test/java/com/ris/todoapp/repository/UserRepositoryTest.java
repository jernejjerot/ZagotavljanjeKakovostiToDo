package com.ris.todoapp.repository;

import com.ris.todoapp.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password123");
        testUser.setAdmin(false);

        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // ------------------- REALNI (pričakovani) SCENARIJI -------------------

    @Test
    @DisplayName("findByEmail – uspešno najde uporabnika po obstoječem e-mailu (PASS)")
    void testFindByEmail_Success() {
        Optional<User> foundUser = userRepository.findByEmail("testuser@example.com");
        assertTrue(foundUser.isPresent(), "User should be found by email.");
        assertEquals("Test", foundUser.get().getName(), "The user's name should match.");
    }

    @Test
    @DisplayName("findByEmail – ne najde uporabnika po neobstoječem e-mailu (PASS)")
    void testFindByEmail_NotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent(), "No user should be found for a non-existent email.");
    }

    @Test
    @DisplayName("save – shrani novega uporabnika in ga je možno poiskati po e-mailu (PASS)")
    void testSaveUser() {
        User newUser = new User();
        newUser.setName("New");
        newUser.setSurname("User");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("newpassword");
        newUser.setAdmin(true);

        User savedUser = userRepository.save(newUser);
        assertNotNull(savedUser.getId(), "Saved user should have a generated ID.");
        assertTrue(userRepository.findByEmail("newuser@example.com").isPresent(),
                "Saved user should be retrievable.");
    }


    @Test
    @DisplayName("save – drugi uporabnik z ISTIM emailom mora pasti na UNIQUE (INTENDED FAIL, če UNIQUE ne obstaja)")
    void testSave_DuplicateEmail() {

        User duplicate = new User();
        duplicate.setName("Dup");
        duplicate.setSurname("User");
        duplicate.setEmail("testuser@example.com");
        duplicate.setPassword("pass");
        duplicate.setAdmin(false);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicate);
            em.flush(); // prisili DB, da preveri constrainte zdaj
        }, "Pričakovana je kršitev UNIQUE(email). Če je test PASS, unikatni indeks verjetno obstaja; če FAIL, omejitev manjka.");
    }

//    @Test
//    @Transactional
//    @DisplayName("save – uporabnik brez e-maila (null) mora pasti na NOT NULL/constraint")
//    void testSaveUser_nullEmail() {
//        User u = new User();
//        u.setName("No");
//        u.setSurname("Email");
//        u.setEmail(null);               // manjkajoč atribut
//        u.setPassword("abc");
//        u.setAdmin(false);
//
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            userRepository.saveAndFlush(u);
//        }, "Pričakovana je kršitev NOT NULL (email). Če test ne pade, v shemi manjka constraint.");
//    }
}