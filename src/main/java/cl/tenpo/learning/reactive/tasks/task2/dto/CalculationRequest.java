package cl.tenpo.learning.reactive.tasks.task2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CalculationRequest(
    @NotNull(message = "number_1 is required") @JsonProperty("number_1") Double number1,
    @NotNull(message = "number_2 is required") @JsonProperty("number_2") Double number2) {}
