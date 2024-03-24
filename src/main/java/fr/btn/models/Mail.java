package fr.btn.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mail {
    @JsonProperty(index = 1)
    private String recipient;
    @JsonProperty(index = 2)
    private String subject;
    @JsonProperty(index = 3)
    private String content;
}
