package jasionowicz.warhammer.builder.SelectedUnit;

import jasionowicz.warhammer.builder.SelectedStats.SelectedStatsDTO;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgradeDTO;
import jasionowicz.warhammer.builder.Unit.UnitDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectedUnitDTO {
    private Integer id;
    private double quantity;
    private String name;
    @Setter
    private SelectedStatsDTO selectedStatsDTO;
    @Setter
    private List<SelectedUpgradeDTO> selectedUpgrades = new ArrayList<>();
    private UnitDTO unit;
    private double totalCost;

}