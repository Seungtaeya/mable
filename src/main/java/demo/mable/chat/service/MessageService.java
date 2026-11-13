package demo.mable.chat.service;

import demo.mable.chat.dto.ChatMessageResponse;
import demo.mable.chat.dto.MessageRequest;
import demo.mable.chat.entity.ChatMessage;
import demo.mable.chat.enums.MessageType;
import demo.mable.chat.repository.MessageRepository;
import demo.mable.room.entity.GameRoom;
import demo.mable.room.exception.GameRoomNotFoundException;
import demo.mable.room.repository.GameRoomRepository;
import demo.mable.user.entity.User;
import demo.mable.user.exception.UserNotFoundException;
import demo.mable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GameRoomRepository gameRoomRepository;

    @Transactional
    public ChatMessageResponse saveLobbyMessage(MessageRequest messageRequest, String username) {
        User sender = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        ChatMessage chatMessage = new ChatMessage(sender, MessageType.CHAT, messageRequest.content());

        ChatMessage save = messageRepository.save(chatMessage);
        return ChatMessageResponse.from(save);
    }

    @Transactional
    public ChatMessageResponse saveGameRoomMessage(Long roomId, MessageRequest messageRequest, String userName) {

        User sender = userRepository.findByUsername(userName).orElseThrow(UserNotFoundException::new);
        GameRoom gameRoom = gameRoomRepository.findById(roomId).orElseThrow(GameRoomNotFoundException::new);
        ChatMessage chatMessage = new ChatMessage(gameRoom,sender,MessageType.CHAT,messageRequest.content());

        ChatMessage save = messageRepository.save(chatMessage);
        return ChatMessageResponse.from(save);
    }


}
