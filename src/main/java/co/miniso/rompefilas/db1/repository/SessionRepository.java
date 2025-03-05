package co.miniso.rompefilas.db1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.miniso.rompefilas.db1.model.Session;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
	Session findByToken(UUID token);
}
