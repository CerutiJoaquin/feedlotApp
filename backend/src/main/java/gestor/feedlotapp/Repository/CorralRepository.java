package gestor.feedlotapp.Repository;

import gestor.feedlotapp.entities.Corral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorralRepository extends JpaRepository<Corral, Integer> {
}
