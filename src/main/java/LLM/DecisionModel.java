package LLM;

import Entity.Item;

public class DecisionModel {
    private final OllamaClient client;
    private final String model;

    // System prompt is sent on every chat call.
    private static final String SYSTEM_PROMPT = """
            You are an impartial reasoning engine for The Higher Lower Game, comparing the monthly Google search volume of two items.
            Objective: Given two items, determine whether Item B likely has a higher or lower monthly Google search volume than Item A. Item A's searches are known; Item B's searches are unknown and must be inferred using general knowledge and reasoning.
            Output rules:
            - Output only one lowercase word: "higher" or "lower".
            - Do not include explanations, reasoning, extra text, or punctuation.
            - Base your choice on popularity, trends, and common sense when exact numbers are unknown.
            
            """;

    public DecisionModel() {
        this(new OllamaClient(), "deepseek-v3.1:671b-cloud"); // change model if needed
    }

    public DecisionModel(OllamaClient client, String model) {
        this.client = client;
        this.model = model;
    }

    // Optional warm-up so the model is pulled/loaded.
    public void startModel() {
        try {
            client.chat(model, SYSTEM_PROMPT, "Respond with only 'ready'.");
            System.out.println("Model is ready.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start model", e);
        }
    }

    public boolean isHigher(Item itemA, Item itemB) {
        // Per the rules, Model.Item B's searches are unknown to force reasoning.
        String userPrompt = """
                Item A: %s — %s
                Item B: %s — unknown
                """.formatted(
                itemA.getSearchTerm(), itemA.getMonthlySearches(),
                itemB.getSearchTerm()
        );

        try {
            String response = client.chat(model, SYSTEM_PROMPT, userPrompt)
                    .trim()
                    .toLowerCase();

            if (response.contains("higher")) return true;
            if (response.contains("lower")) return false;

            System.err.println("Unexpected response from model: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}

