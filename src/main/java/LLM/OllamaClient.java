package LLM;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Lightweight Ollama client using the local REST API.
 *
 * NOTE: This class was created using the help of GPT-5.
 */
public class OllamaClient {

    private final HttpClient http;
    private final String baseUrl;

    public OllamaClient() {
        this("http://localhost:11434");
    }

    public OllamaClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Single chat turn (non-stream) with optional system prompt.
     */
    public String chat(String model, String systemPrompt, String userPrompt) throws IOException, InterruptedException {
        String json = """
                {
                  "model":"%s",
                  "messages":[
                    {"role":"system","content":%s},
                    {"role":"user","content":%s}
                  ],
                  "stream":false
                }
                """.formatted(model, escape(systemPrompt), escape(userPrompt));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        check(resp);
        // Extract "message":{"content":"..."} from the chat response
        System.out.printf(resp.body());
        return extractContent(resp.body());
    }

    /**
     * Convenience overload without system prompt.
     */
    public String chat(String model, String userPrompt) throws IOException, InterruptedException {
        return chat(model, "", userPrompt);
    }

    /**
     * Streaming chat: processes incremental JSON objects line by line.
     */
    public void chatStream(String model, String systemPrompt, String userPrompt, java.util.function.Consumer<String> tokenConsumer)
            throws IOException, InterruptedException {

        String json = """
                {
                  "model":"%s",
                  "messages":[
                    {"role":"system","content":%s},
                    {"role":"user","content":%s}
                  ],
                  "stream":true
                }
                """.formatted(model, escape(systemPrompt), escape(userPrompt));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/chat"))
                .timeout(Duration.ofSeconds(300)) // no timeout for long generations
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<InputStream> resp = http.send(req, HttpResponse.BodyHandlers.ofInputStream());
        if (resp.statusCode() != 200) throw new IOException("HTTP " + resp.statusCode());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resp.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Each line is a JSON object; extract incremental "message":{"content":"..."}
                String part = extractContent(line);
                if (!part.isEmpty()) tokenConsumer.accept(part);
            }
        }
    }

    private void check(HttpResponse<?> resp) throws IOException {
        if (resp.statusCode() != 200) {
            throw new IOException("Ollama error HTTP " + resp.statusCode() + ": " + resp.body());
        }
    }

    // Minimal JSON string escaper (sufficient for prompts). Returns a quoted JSON string.
    private String escape(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '\"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    // Extracts "message":{"content":"..."} from /api/chat responses (including stream chunks).
    private String extractContent(String json) {
        int msgIdx = json.indexOf("\"message\"");
        if (msgIdx < 0) return "";
        int contentIdx = json.indexOf("\"content\"", msgIdx);
        if (contentIdx < 0) return "";
        int colon = json.indexOf(':', contentIdx);
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return "";
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = firstQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '\"' -> sb.append('\"');
                    case '\\' -> sb.append('\\');
                    case 'u' -> {
                        if (i + 4 < json.length()) {
                            String hex = json.substring(i + 1, i + 5);
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                            } catch (NumberFormatException ignored) {}
                            i += 4;
                        }
                    }
                    default -> sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // Example usage
    public static void main(String[] args) throws Exception {
        OllamaClient client = new OllamaClient();
        String system = "You are a concise comparison engine.";
        String user = "Compare Apple vs Samsung search popularity. Respond higher or lower only.";
        String result = client.chat("llama3.1", system, user);
        System.out.println("Result: " + result);

        System.out.println("Streaming:");
        client.chatStream("llama3.1", system, user, chunk -> System.out.print(chunk));
        System.out.println();
    }
}


