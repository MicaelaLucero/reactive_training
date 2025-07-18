package cl.tenpo.learning.reactive.tasks.task2.dto;

public record PercentageResponse(String percentage) {
  public double parsed() {
    return Double.parseDouble(percentage.replace(",", "."));
  }
}