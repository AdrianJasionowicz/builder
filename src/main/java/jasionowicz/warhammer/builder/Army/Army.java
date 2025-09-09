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

    private Double lordPointsLimit;
    private Double heroPointsLimit;
    private Double corePointsLimit;
    private Double specialPointsLimit;
    private Double rarePointsLimit;
    private Double pointsLimit;

    private Double lordPointsUsed;
    private Double heroPointsUsed;
    private Double corePointsUsed;
    private Double specialPointsUsed;
    private Double rarePointsUsed;
    private Double pointsUsed;

    @ManyToOne
    private LoginUser owner;
    @OneToMany(mappedBy = "army", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<SelectedUnit> selectedUnitsList = new ArrayList<>();
}
