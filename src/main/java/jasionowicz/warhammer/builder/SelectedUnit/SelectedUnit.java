package jasionowicz.warhammer.builder.SelectedUnit;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.SelectedStats.SelectedStats;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.Unit.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Component
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectedUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private double quantity;
    @ManyToOne
    private Unit unit;
    @OneToOne
    @JoinColumn(name = "selectedStats_id")
    private SelectedStats selectedStats;
    @OneToMany(mappedBy = "selectedUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SelectedUpgrade> selectedUpgrades = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "army_id")
    private Army army;
    private double totalCost;


        public String getUnitType() {
        if (unit == null) {
            throw new IllegalStateException("Unit is not initialized");
        }
        return unit.getUnitType();
    }

    public SelectedUnit(Unit unit) {
        this.unit = unit;
        this.quantity = unit.getMinQuantity();
        this.selectedStats = new SelectedStats(unit.getUnitStats());
        this.selectedUpgrades = unit.getUpgradesList().stream()
                .map(upgrade -> new SelectedUpgrade(upgrade, this))
                .collect(Collectors.toList());
    }


}