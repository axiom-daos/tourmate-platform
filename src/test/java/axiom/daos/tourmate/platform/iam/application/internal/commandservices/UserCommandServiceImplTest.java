package axiom.daos.tourmate.platform.iam.application.internal.commandservices;

import axiom.daos.tourmate.platform.iam.application.internal.outboundservices.hashing.HashingService;
import axiom.daos.tourmate.platform.iam.application.internal.outboundservices.tokens.TokenService;
import axiom.daos.tourmate.platform.iam.domain.model.aggregates.User;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetAllUsersPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.model.commands.ResetUserPasswordCommand;
import axiom.daos.tourmate.platform.iam.domain.repositories.RoleRepository;
import axiom.daos.tourmate.platform.iam.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for password reset operations in {@link UserCommandServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RoleRepository roleRepository;

    private UserCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserCommandServiceImpl(userRepository, hashingService, tokenService, roleRepository);
    }

    @Test
    @DisplayName("Should reset a user's password by username")
    void shouldResetUserPasswordByUsername() {
        var existingUser = User.reconstitute(1L, "guide.cusco", "old-hash", Set.of());
        when(userRepository.findByUsername("guide.cusco")).thenReturn(Optional.of(existingUser));
        when(hashingService.encode("12345678")).thenReturn("$2a$10$encoded12345678");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new ResetUserPasswordCommand(null, "guide.cusco", "12345678"));

        assertTrue(result.isSuccess());
        assertEquals("$2a$10$encoded12345678", result.toOptional().orElseThrow().getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Should reset all existing users' passwords to 12345678")
    void shouldResetAllExistingUsersPasswords() {
        var u1 = User.reconstitute(1L, "admin", "old-1", Set.of());
        var u2 = User.reconstitute(2L, "guide", "old-2", Set.of());
        var u3 = User.reconstitute(3L, "tourist", "old-3", Set.of());
        when(userRepository.findAll()).thenReturn(List.of(u1, u2, u3));
        when(hashingService.encode("12345678")).thenReturn("$2a$10$encoded12345678");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new ResetAllUsersPasswordCommand("12345678"));

        assertTrue(result.isSuccess());
        assertEquals(3, result.toOptional().orElseThrow());
        assertEquals("$2a$10$encoded12345678", u1.getPassword());
        assertEquals("$2a$10$encoded12345678", u2.getPassword());
        assertEquals("$2a$10$encoded12345678", u3.getPassword());
        verify(userRepository, times(3)).save(any(User.class));
    }
}
