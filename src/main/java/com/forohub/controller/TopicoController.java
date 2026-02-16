package com.forohub.controller;

import com.forohub.domain.topico.DatosListadoTopico;
import com.forohub.domain.topico.DatosRegistroTopico;
import com.forohub.domain.topico.Topico;
import com.forohub.domain.topico.TopicoRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
@Validated
public class TopicoController {

    private final TopicoRepository topicoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Void> registrar(@RequestBody @Valid DatosRegistroTopico datos) {
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un topico con el mismo titulo y mensaje");
        }
        topicoRepository.save(new Topico(datos));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listar(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) Integer anio,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable paginacion
    ) {
        Page<DatosListadoTopico> pagina = obtenerPagina(curso, anio, paginacion)
                .map(DatosListadoTopico::new);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosListadoTopico> detallar(@PathVariable @Positive Long id) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topico no encontrado"));
        return ResponseEntity.ok(new DatosListadoTopico(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosListadoTopico> actualizar(
            @PathVariable @Positive Long id,
            @RequestBody @Valid DatosRegistroTopico datos
    ) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (!topicoOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topico no encontrado");
        }

        if (topicoRepository.existsByTituloAndMensajeAndIdNot(datos.titulo(), datos.mensaje(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un topico con el mismo titulo y mensaje");
        }

        Topico topico = topicoOptional.get();
        topico.actualizar(datos);
        return ResponseEntity.ok(new DatosListadoTopico(topico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (!topicoOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topico no encontrado");
        }

        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Page<Topico> obtenerPagina(String curso, Integer anio, Pageable paginacion) {
        if (curso != null && anio != null) {
            return topicoRepository.findByCursoIgnoreCaseAndFechaCreacionBetween(
                    curso,
                    inicioDeAnio(anio),
                    inicioDeAnio(anio + 1),
                    paginacion
            );
        }
        if (curso != null) {
            return topicoRepository.findByCursoIgnoreCase(curso, paginacion);
        }
        if (anio != null) {
            return topicoRepository.findByFechaCreacionBetween(
                    inicioDeAnio(anio),
                    inicioDeAnio(anio + 1),
                    paginacion
            );
        }
        return topicoRepository.findAll(paginacion);
    }

    private LocalDateTime inicioDeAnio(int anio) {
        return LocalDateTime.of(anio, 1, 1, 0, 0);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(Map.of("error", "Parametro invalido", "detalle", ex.getMessage()));
    }
}
