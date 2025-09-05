package jasionowicz.warhammer.builder.Army;

import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArmyRepository extends JpaRepository<Army, Long> {
    List<Army> findByOwner(LoginUser owner);

}
