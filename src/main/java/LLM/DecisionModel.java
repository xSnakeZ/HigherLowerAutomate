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
            - Output consists of two elements. The first element is either "higher" or "lower", indicating whether Item B has a higher or lower search volume than Item A. The second element is a brief explanation (1-2 sentences) of your reasoning. Those two elements are separated by a semicolon.
            - Base your choice on popularity, trends, and common sense when exact numbers are unknown.
            Example output: "higher; Item B is a globally popular brand with widespread recognition, likely leading to higher search volume."
            
            """;

    public DecisionModel() {this(new OllamaClient(), Model.gpt_oss_120b_cloud); // change model if needed
    }

    public DecisionModel(OllamaClient client, Model model) {
        this.client = client;
        this.model = model.toString();
    }
    public void startModel() {
        try {
            client.chat(model, SYSTEM_PROMPT, "Respond with only 'ready'.");
            System.out.println("Model is ready.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start model", e);
        }
    }

    public boolean isHigher(Item itemA, Item itemB) {
        String userPrompt = """
                Item A: %s — %s
                Item B: %s — unknown
                """.formatted(
                itemA.getSearchTerm(), itemA.getMonthlySearches(),
                itemB.getSearchTerm()
        );

        String response = "";

        try {
            response = client.chat(model, SYSTEM_PROMPT, userPrompt)
                    .trim()
                    .toLowerCase();

            System.out.println("Reason: " + response.substring(response.indexOf(';') + 1).trim());

            if (response.contains("higher")) return true;
            if (response.contains("lower")) return false;

            System.err.println("Unexpected response from model: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Could not determine higher/lower from model response: " + response);
    }
}

