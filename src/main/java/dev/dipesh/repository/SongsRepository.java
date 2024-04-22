package dev.dipesh.repository;

import dev.dipesh.entity.SongsMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongsRepository extends JpaRepository<SongsMetadata, String> {
}

public class SongsRepository {
}
