package fun.jaobabus.commandlib.util;

public interface AbstractMessage
{
    class StringMessage implements AbstractMessage {
        private final String msg;

        public StringMessage(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    static AbstractMessage fromString(String str) {
        return new StringMessage(str);
    }

    default String toJson() {
        var s = toString().replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"text\":\"" + s + "\"}";
    }
}
