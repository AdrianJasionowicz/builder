package jasionowicz.warhammer.builder.SelectedUpgrade;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.Upgrade.Upgrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SelectedUpgrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private double quantity;
    private boolean selected;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SelectedUnits_id")
    @JsonBackReference
    private SelectedUnit selectedUnit;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "upgrade_id")
    private Upgrade upgrade;

    public SelectedUpgrade(Upgrade upgrade, SelectedUnit selectedUnit) {
        this.selectedUnit = selectedUnit;
        this.upgrade = upgrade;
    }
}
