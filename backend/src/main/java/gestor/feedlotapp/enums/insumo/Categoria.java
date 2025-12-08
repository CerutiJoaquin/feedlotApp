package gestor.feedlotapp.enums.insumo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Categoria {
    MEDICAMENTO("Medicamento"),
    ALIMENTO("Alimento");

    private final String label;

    Categoria(String label) { this.label = label; }

    @JsonValue
    public String getLabel() { return label; }

    @JsonCreator
    public static Categoria from(String value) {
        if (value == null) return null;
        for (var t : values()) {
            if (t.label.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo inválido: " + value);
    }
}

