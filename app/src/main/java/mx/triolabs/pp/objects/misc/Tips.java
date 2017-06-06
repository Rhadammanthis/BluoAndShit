package mx.triolabs.pp.objects.misc;

/**
 * Created by hugomedina on 12/1/16.
 */

public class Tips {

    public enum Types {
        EXERCISE("3"), HEALTH("2"), NUTRITION("1");

        private final String text;

        /**
         * @param text
         */
         Types(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    private Types type;
    private String title;
    private String content;

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
