package jasionowicz.warhammer.builder.UnitStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitStatsRepository extends JpaRepository<UnitStats, Long> {
}
