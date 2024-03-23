package site.milanchen.chat.utils;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author milan
 * @description
 */
public class DocumentParseUtil {

    private static final int MAX_LENGTH = 200;

    public static List<String> parse(InputStream inputStream) throws Exception {
        try (InputStream stream = inputStream) {
            String text = extractTextWithTika(stream);
            List<String> paragraphs = divideTextIntoParagraphs(text);
            return paragraphs;
        }
    }

    private static String extractTextWithTika(InputStream stream) throws Exception {
        Parser autoDetectParser = new AutoDetectParser();
        ParseContext context = new ParseContext();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        autoDetectParser.parse(stream, handler, metadata, context);
        String text = handler.toString();
        return text.replaceAll("\\s+", "");
    }

    private static List<String> divideTextIntoParagraphs(String text) {
        String[] sentences = text.split("[.。!！?？]+");
        List<String> paragraphs = new ArrayList<>();
        int currentLength = 0;
        StringBuilder currentParagraph = new StringBuilder();
        for (String sentence : sentences) {
            if (currentLength + sentence.length() <= MAX_LENGTH) {
                currentParagraph.append(sentence).append(".");
                currentLength += (sentence.length() + 1);
            } else {
                paragraphs.add(currentParagraph.toString().trim());
                currentParagraph = new StringBuilder(sentence + ".");
                currentLength = (sentence.length() + 1);
            }
        }
        paragraphs.add(currentParagraph.toString().trim());
        return paragraphs;
    }

}
