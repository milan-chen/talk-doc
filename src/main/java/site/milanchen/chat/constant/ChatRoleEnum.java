package site.milanchen.chat.constant;

/**
 * @author milan
 * @description
 */
public enum ChatRoleEnum {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    ;

    private String code;

    ChatRoleEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
