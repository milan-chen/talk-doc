package site.milanchen.chat.utils;

import org.springframework.stereotype.Component;
import site.milanchen.chat.component.TimerExpireHashMap;
import site.milanchen.chat.constant.ChatRoleEnum;
import site.milanchen.chat.dto.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author milan
 * @description
 */
@Component
public class UserSessionUtil {

    private static final long EXPIRE_TIME_SECOND = 30 * 60;
    private static final TimerExpireHashMap<String, List<Message>> MESSAGE_HISTORY = new TimerExpireHashMap<>();
    private static final TimerExpireHashMap<String, Integer> IP_DOC_UPLOAD_TIMES = new TimerExpireHashMap<>();
    private static final List<String> DOC_UPLOAD_RECORD = new ArrayList<>();

    public static void addUploadRecord(String sessionId) {
        DOC_UPLOAD_RECORD.add(sessionId);
    }

    public static boolean existUploadRecord(String sessionId) {
        return DOC_UPLOAD_RECORD.contains(sessionId);
    }

    public static void addIpUploadTimes(String ip, long expireTime) {
        Integer times = getIpUploadTimes(ip);
        IP_DOC_UPLOAD_TIMES.put(ip, times + 1, expireTime);
    }

    public static Integer getIpUploadTimes(String ip) {
        Integer times = IP_DOC_UPLOAD_TIMES.get(ip);
        return times == null ? 0 : times;
    }

    public static void addMessage(String sessionId, Message message) {
        List<Message> messageList = MESSAGE_HISTORY.getOrDefault(sessionId, systemPrompt());
        messageList.add(message);
        MESSAGE_HISTORY.put(sessionId, messageList, EXPIRE_TIME_SECOND);
    }

    public static List<Message> getHistory(String sessionId, Integer maxTokens) {
        List<Message> history = MESSAGE_HISTORY.getOrDefault(sessionId, systemPrompt());
        List<Message> result = new ArrayList<>();
        int count = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            Message message = history.get(i);
            count += message.getContent().length();
            if (count >= maxTokens) {
                break;
            }
            result.add(message);
        }
        Collections.reverse(result);
        return result;
    }


    public static void clearHistory(String sessionId) {
        MESSAGE_HISTORY.remove(sessionId);
        DOC_UPLOAD_RECORD.remove(sessionId);
    }

    private static List<Message> systemPrompt() {
        Message message = Message.builder()
                .role(ChatRoleEnum.SYSTEM.getCode())
                .content("你是一名专业的问答机器人，需要基于事实地给出正确的答案，并且只做被指示的事情，不能编造事情。在做出回应时，请必须确保使用Markdown语法来合适地样式化你的回应。使用Markdown语法进行诸如标题、列表、颜色文字、代码块、高亮等的样式化。确保在你实际的回应中不要提及Markdown或样式化。")
                .build();
        return new ArrayList<>(Collections.singleton(message));
    }

}