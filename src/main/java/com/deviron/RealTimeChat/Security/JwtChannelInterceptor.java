
package com.deviron.RealTimeChat.Security;

import com.deviron.RealTimeChat.Repository.UserRepository;
import com.deviron.RealTimeChat.Repository.ConversationMenmbersRepository;

import io.jsonwebtoken.JwtException;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final ConversationMenmbersRepository memberRepository;

    public JwtChannelInterceptor(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            UserRepository userRepository,
            ConversationMenmbersRepository memberRepository
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message, StompHeaderAccessor.class
                );

        if (accessor == null) {
            return message;
        }

        // 1. Authenticate the STOMP connection
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authorizationHeader =
                    accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader == null ||
                    !authorizationHeader.startsWith("Bearer ")) {
                throw new AccessDeniedException(
                        "Missing or invalid Authorization header"
                );
            }

            String token = authorizationHeader.substring(7);

            try {
                String email = jwtService.extractEmail(token);

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                if (!jwtService.isTokenValid(token, userDetails)) {
                    throw new AccessDeniedException("Invalid JWT token");
                }

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                accessor.setUser(authentication);

            } catch (JwtException | UsernameNotFoundException |
                     IllegalArgumentException exception) {

                throw new AccessDeniedException(
                        "WebSocket authentication failed",
                        exception
                );
            }
        }

        // 2. Authorize conversation subscriptions
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            String destination = accessor.getDestination();

            if (destination != null &&
                    destination.startsWith("/topic/conversation/")) {

                Principal principal = accessor.getUser();
                System.out.println("===== SUBSCRIPTION DEBUG =====");
                System.out.println("Destination: " + accessor.getDestination());
                System.out.println("Principal: " +
                        (principal == null ? "NULL" : principal.getName()));
                System.out.println("Command: " + accessor.getCommand());

                if (principal == null) {
                    throw new AccessDeniedException(
                            "You must authenticate first"
                    );
                }

                String email = principal.getName();

                Long conversationId;

                try {
                    conversationId = Long.parseLong(
                            destination.substring(
                                    "/topic/conversation/".length()
                            )
                    );
                } catch (NumberFormatException exception) {
                    throw new AccessDeniedException(
                            "Invalid conversation destination"
                    );
                }

                var user = userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new AccessDeniedException("User not found")
                        );

                System.out.println("Database User ID: " + user.getId());

                System.out.println("Conversation ID: " + conversationId);

                boolean isMember =
                        memberRepository.existsByConversationIdAndUserId(
                                conversationId,
                                user.getId()
                        );

                System.out.println("Is conversation member: " + isMember);

                if (!isMember) {
                    throw new AccessDeniedException(
                            "You are not a member of this conversation"
                    );
                }
            }
        }

        return message;
    }
}
