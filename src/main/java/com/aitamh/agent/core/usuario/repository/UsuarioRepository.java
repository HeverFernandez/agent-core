package com.aitamh.agent.core.usuario.repository;

import com.aitamh.agent.core.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Entity Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    Page<Usuario> findByActivo(Boolean activo, Pageable pageable);

    Page<Usuario> findByRolAndActivo(String rol, Boolean activo, Pageable pageable);

    Page<Usuario> findByEstadoAndActivo(Boolean estado, Boolean activo, Pageable pageable);

    List<Usuario> findByRolAndActivoAndEstado(String rol, Boolean activo, Boolean estado);
}

