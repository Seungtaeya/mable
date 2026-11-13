package demo.mable.chat.dto;

import demo.mable.chat.entity.ChatMessage;
import demo.mable.chat.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponse {

    private Long id;
    private String senderName;
    private MessageType messageType;
    private String content;

    public ChatMessageResponse(Long id, String senderName, MessageType messageType, String content) {
        this.id = id;
        this.senderName = senderName;
        this.messageType = messageType;
        this.content = content;
    }

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(chatMessage.getId(),
                chatMessage.getSender().getUsername(),
                chatMessage.getMessageType(),
                chatMessage.getContent());
    }
}
