package jasionowicz.warhammer.builder.Army;

import jakarta.persistence.*;
import jasionowicz.warhammer.builder.LoginUser.LoginUser;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Army {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne
    private LoginUser owner;
    @OneToMany(mappedBy = "army", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectedUnit> selectedUnitsList = new ArrayList<>();
}
