package jasionowicz.warhammer.builder.SelectedUpgrade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectedUpgradeRepository extends JpaRepository<SelectedUpgrade, Integer> {
    List<SelectedUpgrade> findAllBySelectedUnitId(Integer unitId);


}
