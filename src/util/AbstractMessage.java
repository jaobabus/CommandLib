package util;

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
}
