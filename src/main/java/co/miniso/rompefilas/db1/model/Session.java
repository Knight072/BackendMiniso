package co.miniso.rompefilas.db1.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "session")
public class Session {
	@Id
	@Column(name = "token", nullable = false, unique = true)
	private UUID token;

	@Column(name = "timestamp", nullable = false)
	private Instant timestamp;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "document", nullable = false)
	private Login user;

	public Session() {
	}

	public Session(UUID randomUUID, Instant now, Login user) {
		this.token = randomUUID;
		this.timestamp = now;
		this.user = user;
	}

	public UUID getToken() {
		return token;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public Login getDocument() {
		return user;
	}

	public void setToken(UUID token) {
		this.token = token;
	}

	public void setToken(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public void setDocument(Login user) {
		this.user = user;
	}

}
