package jasionowicz.warhammer.builder.Army;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import jasionowicz.warhammer.builder.LoginUser.LoginUserDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArmyDTO {
    private long id;
    private String factionName;
    private String name;
    private String description;
    private Double pointsLimit;

    private Double lordPointsLimit;
    private Double heroPointsLimit;
    private Double corePointsLimit;
    private Double specialPointsLimit;
    private Double rarePointsLimit;

    private Double lordPointsUsed;
    private Double heroPointsUsed;
    private Double corePointsUsed;
    private Double specialPointsUsed;
    private Double rarePointsUsed;
    private Double pointsUsed;

    private LoginUserDTO owner;
    private List<SelectedUnitDTO> selectedUnitsList = new ArrayList<>();
}
