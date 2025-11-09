package LLM;

public enum Model {
    gpt_oss_120b_cloud,
    glm_4_6_cloud,
    deepseek_v3_1_671b_cloud,
    kimi_k2_thinking_cloud;

    @Override
    public String toString() {
        return switch (this) {
            case gpt_oss_120b_cloud -> "gpt-oss:120b-cloud";
            case glm_4_6_cloud -> "glm-4.6:cloud";
            case deepseek_v3_1_671b_cloud -> "deepseek-v3.1:671b-cloud";
            case kimi_k2_thinking_cloud -> "kimi-k2-thinking:cloud";
        };
    }
}
