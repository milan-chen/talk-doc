package site.milanchen.chat.constant;

/**
 * @author milan
 * @description
 */
public enum ChatModeEnum {

    FOCUS("focus"),
    FREE("free"),
    ;

    private String code;

    ChatModeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
