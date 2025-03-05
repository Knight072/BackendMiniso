package co.miniso.rompefilas.db1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.miniso.rompefilas.db1.model.Login;

@Repository
public interface LoginRepository extends JpaRepository<Login, String>{
	Optional<Login> findByDocument(String document);
}