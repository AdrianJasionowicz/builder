package jasionowicz.warhammer.builder.SelectedUpgrade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectedUpgradeDTO {
    private Integer id;
    private double quantity;
    private boolean selected;
    private Double totalCost;
    private String name;
    private double pointsCost;




    public SelectedUpgradeDTO(SelectedUpgrade selectedUpgrade) {
        this.id = selectedUpgrade.getId();
        this.quantity = selectedUpgrade.getQuantity();
        this.selected = selectedUpgrade.isSelected();
        this.name = selectedUpgrade.getUpgrade().getName();
        this.pointsCost = selectedUpgrade.getUpgrade().getPointsCost();
    }

}