package com.forohub.domain.topico;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensaje(String titulo, String mensaje);
    boolean existsByTituloAndMensajeAndIdNot(String titulo, String mensaje, Long id);

    Page<Topico> findByCursoIgnoreCase(String curso, Pageable pageable);

    Page<Topico> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    Page<Topico> findByCursoIgnoreCaseAndFechaCreacionBetween(
            String curso,
            LocalDateTime inicio,
            LocalDateTime fin,
            Pageable pageable
    );
}
