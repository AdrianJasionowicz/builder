package jasionowicz.warhammer.builder.SelectedStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectedStatsRepository extends JpaRepository<SelectedStats, Integer> {
}
