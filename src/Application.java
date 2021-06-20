import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
    public static void main(String[] args) {
        if (args.length > 0){
            String fileName = args[0];
        } else {
            System.err.println("Use syntax: Application [file_name]");
            return;
        }
        Path inputPath = Paths.get("resources/" + args[0]);
        Path outputPath = Paths.get("resources/out.txt");
        Charset charset = StandardCharsets.UTF_8;

        Pattern nStrPattern = Pattern.compile("\\bnstr\\(\\s*\"(.*?)\".*?\\)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern phrasesPattern = Pattern.compile("(\\w+)\\s*=\\s*'(.*?)'", Pattern.DOTALL);

        try (var lineNumberReader = new LineNumberReader(Files.newBufferedReader(inputPath, charset));
             var outputWriter = Files.newBufferedWriter(outputPath, charset, StandardOpenOption.CREATE)){
            Matcher mainMatcher = nStrPattern.matcher(Files.readString(inputPath, charset));
            long prevPos = 0;
            while (mainMatcher.find()){
                lineNumberReader.skip(mainMatcher.start() - prevPos);
                prevPos = mainMatcher.start();

                Matcher phrasesMatcher = phrasesPattern.matcher(mainMatcher.group(1));
                while (phrasesMatcher.find()) {
                    outputWriter.write(String.format("%d: %s: %s%n", lineNumberReader.getLineNumber() + 1,
                            phrasesMatcher.group(1), phrasesMatcher.group(2)));
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}