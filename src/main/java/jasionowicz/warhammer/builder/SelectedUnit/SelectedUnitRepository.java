package jasionowicz.warhammer.builder.SelectedUnit;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectedUnitRepository extends JpaRepository<SelectedUnit, Integer> {
    List<SelectedUnit> findAll();

    @Query("select  u from SelectedUnit u where u.unit.id=:id")
    List<SelectedUnit> findAllByUnitId(Integer id);
}
