package com.forohub.domain.topico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DatosRegistroTopico(
        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotBlank
        @Size(max = 2000)
        String mensaje,

        @NotBlank
        @Size(max = 100)
        String autor,

        @NotBlank
        @Size(max = 120)
        String curso
) {
}
