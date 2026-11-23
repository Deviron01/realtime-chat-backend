package com.deviron.RealTimeChat.Service.IMPL;

import com.deviron.RealTimeChat.DTO.Conversation.ConversationMemberDTO;
import com.deviron.RealTimeChat.DTO.Conversation.ConversationResponseDTO;
import com.deviron.RealTimeChat.DTO.Conversation.CreateGroupDTO;
import com.deviron.RealTimeChat.Entity.Conversation;
import com.deviron.RealTimeChat.Entity.ConversationMembers;
import com.deviron.RealTimeChat.Entity.User;
import com.deviron.RealTimeChat.Enum.ConversationType;
import com.deviron.RealTimeChat.Repository.ConversationMenmbersRepository;
import com.deviron.RealTimeChat.Repository.ConversationRepository;
import com.deviron.RealTimeChat.Repository.UserRepository;
import com.deviron.RealTimeChat.Service.ConversationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMenmbersRepository memberRepository;
    private final UserRepository userRepository;

    public ConversationServiceImpl(
            ConversationRepository conversationRepository,
            ConversationMenmbersRepository memberRepository,
            UserRepository userRepository) {

        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public ConversationResponseDTO createPrivateConversation(
            String currentUserEmail,
            Long recipientId) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Current user not found"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Recipient not found"));

        if (currentUser.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException(
                    "You cannot create a conversation with yourself");
        }

        List<ConversationMembers> currentMemberships =
                memberRepository.findByUserId(currentUser.getId());

        for (ConversationMembers membership : currentMemberships) {

            Conversation conversation = membership.getConversation();

            if (conversation.getType() == ConversationType.PRIVATE &&
                    memberRepository.existsByConversationIdAndUserId(
                            conversation.getId(), recipient.getId())) {

                return convertToDTO(conversation);
            }
        }

        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.PRIVATE);

        Conversation savedConversation =
                conversationRepository.save(conversation);

        ConversationMembers firstMember = new ConversationMembers();
        firstMember.setConversation(savedConversation);
        firstMember.setUser(currentUser);

        ConversationMembers secondMember = new ConversationMembers();
        secondMember.setConversation(savedConversation);
        secondMember.setUser(recipient);

        memberRepository.save(firstMember);
        memberRepository.save(secondMember);

        return convertToDTO(savedConversation);
    }

    private ConversationResponseDTO convertToDTO(Conversation conversation) {

        return new ConversationResponseDTO(
                conversation.getId(),
                conversation.getName(),
                conversation.getType(),
                conversation.getCreatedAt()
        );
    }
    @Override
    @Transactional
    public ConversationResponseDTO createGroupConversation(
            String currentUserEmail,
            CreateGroupDTO request) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Current user not found"));

        Conversation conversation = new Conversation();
        conversation.setName(request.getName());
        conversation.setType(ConversationType.GROUP);

        Conversation savedConversation =
                conversationRepository.save(conversation);

        // Add the group creator
        ConversationMembers creator = new ConversationMembers();
        creator.setConversation(savedConversation);
        creator.setUser(currentUser);

        memberRepository.save(creator);

        // Add the other members
        for (Long memberId : request.getMemberIds()) {

            if (memberId.equals(currentUser.getId())) {
                continue;
            }

            User member = userRepository.findById(memberId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "User not found with ID: " + memberId));

            ConversationMembers conversationMember =
                    new ConversationMembers();

            conversationMember.setConversation(savedConversation);
            conversationMember.setUser(member);

            memberRepository.save(conversationMember);
        }

        return convertToDTO(savedConversation);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getUserConversations(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return memberRepository.findByUserId(user.getId())
                .stream()
                .map(ConversationMembers::getConversation)
                .map(this::convertToDTO)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public ConversationResponseDTO getConversationDetails(
            String email,
            Long conversationId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (!memberRepository.existsByConversationIdAndUserId(
                conversationId, user.getId())) {

            throw new IllegalArgumentException(
                    "You are not a member of this conversation");
        }

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Conversation not found"));

        return convertToDTO(conversation);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ConversationMemberDTO> getConversationMembers(
            String email,
            Long conversationId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (!memberRepository.existsByConversationIdAndUserId(
                conversationId, user.getId())) {

            throw new IllegalArgumentException(
                    "You are not a member of this conversation");
        }

        return memberRepository.findByConversationId(conversationId)
                .stream()
                .map(member -> new ConversationMemberDTO(
                        member.getUser().getId(),
                        member.getUser().getUsername(),
                        member.getUser().getEmail()
                ))
                .toList();
    }
}