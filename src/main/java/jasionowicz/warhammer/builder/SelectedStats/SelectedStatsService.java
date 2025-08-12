package jasionowicz.warhammer.builder.SelectedStats;

import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectedStatsService {

    private SelectedUnitRepository selectedUnitRepository;
    private SelectedStatsRepository selectedStatsRepository;

    public SelectedStatsService(SelectedUnitRepository selectedUnitRepository, SelectedStatsRepository selectedStatsRepository) {
        this.selectedUnitRepository = selectedUnitRepository;
        this.selectedStatsRepository = selectedStatsRepository;
    }


    public void setUnitStats(int selectedId) {
        SelectedUnit selectedUnit = selectedUnitRepository.getReferenceById(selectedId);
        int statsId = selectedUnit.getSelectedStats().getId();
        List<SelectedUpgrade> selectedUpgradeList = selectedUnit.getSelectedUpgrades();
        SelectedStats selectedStats = selectedStatsRepository.getReferenceById(statsId);
        for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
            if (selectedUpgrade.isSelected()) {
                if (selectedUpgrade.getUpgrade().getName().equalsIgnoreCase("Light Armor")) {
                    selectedUnit.getSelectedStats().setBasicSave(6);
                }
                if (selectedUpgrade.getUpgrade().getName().equalsIgnoreCase("Heavy Armor")) {
                    selectedUnit.getSelectedStats().setBasicSave(5);
                }
                if (selectedUpgrade.getUpgrade().getName().equalsIgnoreCase("Shield")) {
                    int basicSave = selectedUnit.getSelectedStats().getBasicSave();
                    selectedUnit.getSelectedStats().setBasicSave(basicSave - 1);
                    Integer wardSave = selectedUnit.getSelectedStats().getWardSave();
                    if (wardSave == 0 || wardSave == null) {
                        selectedUnit.getSelectedStats().setWardSave(6);
                    } else {
                        selectedUnit.getSelectedStats().setWardSave(wardSave - 1);
                    }
                }
            }


        }


    }


}
