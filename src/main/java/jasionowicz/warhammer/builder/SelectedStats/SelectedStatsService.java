package jasionowicz.warhammer.builder.SelectedStats;

import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyRepository;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitRepository;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import jasionowicz.warhammer.builder.UnitStats.UnitStatsRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelectedStatsService {

    private final ArmyRepository armyRepository;
    private final SelectedUnit selectedUnit;
    private final SelectedStatsRepository selectedStatsRepository;
    private final UnitStatsRepository unitStatsRepository;
    private final SelectedUnitRepository selectedUnitRepository;

    public SelectedStatsService(ArmyRepository armyRepository, SelectedUnit selectedUnit, SelectedStatsRepository selectedStatsRepository, UnitStatsRepository unitStatsRepository, UnitStatsRepository unitStatsRepository1, SelectedUnitRepository selectedUnitRepository) {
        this.armyRepository = armyRepository;
        this.selectedUnit = selectedUnit;
        this.selectedStatsRepository = selectedStatsRepository;
        this.unitStatsRepository = unitStatsRepository1;
        this.selectedUnitRepository = selectedUnitRepository;
    }

    public void adjustArmyStatsToSelectedUpgrades(Long armyId) {
        Army army = armyRepository.findById(armyId).orElseThrow();
        List<SelectedUnit> selectedUnitList = army.getSelectedUnitsList();
        for (SelectedUnit selectedUnit : selectedUnitList) {
            setBasicStatsBeforeAddUpgradeBuffs(selectedUnit);
            setArmourStats(selectedUnit);
            setWeaponsStats(selectedUnit);
            setSkillsStats(selectedUnit);

        }
    }

    public void setBasicStatsBeforeAddUpgradeBuffs(SelectedUnit selectedUnit) {
        UnitStats basic = unitStatsRepository.findById(selectedUnit.getUnit().getUnitStats().getId()).orElseThrow();
        SelectedStats old = selectedStatsRepository.getReferenceById(selectedUnit.getSelectedStats().getId());
        old.setWs(basic.getWs());
        old.setBs(basic.getBs());
        old.setS(basic.getS());
        old.setT(basic.getT());
        old.setW(basic.getW());
        old.setA(basic.getA());
        old.setBasicSave(basic.getBasicSave());
        old.setWardSave(basic.getWardSave());
        old.setI(basic.getI());
        old.setM(basic.getM());
        old.setLd(basic.getLd());
        selectedStatsRepository.save(old);
    }


    private void setArmourStats(SelectedUnit selectedUnit) {
        SelectedStats selectedStats = selectedStatsRepository.getReferenceById(selectedUnit.getId());
        List<SelectedUpgrade> selectedUpgrades = selectedUnit.getSelectedUpgrades().stream()
                .filter(SelectedUpgrade::isSelected)
                .toList();

        int baseArmour = 7;
        int ward = 7;

        for (SelectedUpgrade upgrade : selectedUpgrades) {
            switch (upgrade.getUpgrade().getName()) {
                case "Light Armour": baseArmour = Math.min(baseArmour, 6); break;
                case "Heavy Armour": baseArmour = Math.min(baseArmour, 5); break;
                case "Armour of Destiny": baseArmour = 5; ward = Math.min(ward, 4); break;
                case "Armour of Silvered Steel": baseArmour = 2; break;
                case "Armour of Fortune": baseArmour = 5; ward = Math.min(ward, 5); break;
                case "Glittering Scales": baseArmour = 6; break;
                case "Gamblers Armour": baseArmour = 5; ward = Math.min(ward, 6); break;
            }


        }

        for (SelectedUpgrade upgrade : selectedUpgrades) {
            switch (upgrade.getUpgrade().getName()) {

                case "Shield":
                case "Shields":
                    baseArmour = safeCalculate(baseArmour, 1); ward = safeCalculate(ward,1); break;
                case "Tricksters helm":
                case "Helm of Discord":
                case "Dragonhelm": baseArmour = safeCalculate(baseArmour, 1); break;
                case "Charmed Shield": baseArmour = safeDecrement(baseArmour, 1, 6); ward = Math.min(ward, 6); break;
                case "Enchanted Shield": baseArmour = safeDecrement(baseArmour, 2, 6); ward = Math.min(ward, 6); break;
                case "Shield of Ptolos": baseArmour = safeDecrement(baseArmour, 1, 6); ward = Math.min(ward, 6); break;
                case "Shield of Distraction": baseArmour = safeDecrement(baseArmour, 1, 6); ward = Math.min(ward, 6); break;
                case "Spellshield": baseArmour = safeDecrement(baseArmour, 1, 6); break;
                case "Warpstone armour": baseArmour = 4; break;
                case "Worlds Edge Armour": baseArmour =4;break;

            }

        }

        for (SelectedUpgrade upgrade : selectedUpgrades) {
            switch (upgrade.getUpgrade().getName()) {
                case "Talisman of Preservation": ward = Math.min(ward, 4); break;
                case "Talisman of Endurance": ward = Math.min(ward, 5); break;
                case "Foul Pendant": ward = Math.min(ward, 5); break;
                case "Ward save (5+)": ward = Math.min(ward, 5); break;
                case "Talisman of Protection": ward = Math.min(ward, 6); break;
            }
        }

        for (SelectedUpgrade upgrade : selectedUpgrades) {
            switch (upgrade.getUpgrade().getName()) {
                case "Rat Ogre Bonebreaker":
                case "Great Pox Rat":
                case "War-litter": baseArmour = safeDecrement(baseArmour, 1, 6); break;
                case "Screaming Bell": ward = Math.min(ward, 4); break;
            }
        }

        selectedStats.setBasicSave(baseArmour == 7 ? null : baseArmour);
        selectedStats.setWardSave(ward == 7 ? null : ward);
        selectedStatsRepository.save(selectedStats);
    }

    public void setWeaponsStats(SelectedUnit selectedUnit) {
        SelectedStats selectedStats = selectedStatsRepository.getReferenceById(selectedUnit.getId());
        List<SelectedUpgrade> selectedUpgradeList = selectedUnit.getSelectedUpgrades().stream()
                .filter(SelectedUpgrade::isSelected)
                .toList();
        for (SelectedUpgrade upgrade : selectedUpgradeList) {
        switch (upgrade.getUpgrade().getName()) {
            case "Fellblade":
                selectedStats.setS(10);
                break;
            case "Warlock-Augumented Weapon":
                selectedStats.setS(selectedStats.getS()+1);
                selectedStats.setA(selectedStats.getA()+1);
            break;
            case "Blade of Corruption":
                selectedStats.setS(selectedStats.getS()+1);
                break;
            case "Dwarfbane":
                selectedStats.setS(selectedStats.getS()+1);
                break;
            case "Warlock Optics":
                selectedStats.setBs(selectedStats.getBs()+1);
            case "Halberd":
                selectedStats.setS(selectedStats.getS()+1);
                break;
            case "Great weapon":
                selectedStats.setS(selectedStats.getS()+2);
                break;
            case "Giant Blade":
                selectedStats.setBs(selectedStats.getBs()+3);
                break;
            case "Sword of Bloodshed":
                selectedStats.setA(selectedStats.getA()+3);
                break;
            case "Ogre Blade":
                selectedStats.setS(selectedStats.getS()+2);
                break;
            case "Sword of Strife":
                selectedStats.setA(selectedStats.getA()+2);
                break;
            case "Fencers Blades":
                selectedStats.setWs(10);
                break;
            case "Sword of Battle":
                selectedStats.setA(selectedStats.getA()+1);
                break;
            case "Sword of Might":
                selectedStats.setS(selectedStats.getS()+1);
                break;
            case "Gold Sigil Sword":
                selectedStats.setI(10);
                break;
        }
        }
        selectedStatsRepository.save(selectedStats);
        }
    public void setSkillsStats(SelectedUnit selectedUnit) {
        SelectedStats selectedStats = selectedStatsRepository.getReferenceById(selectedUnit.getId());
        List<SelectedUpgrade> selectedUpgradeList = selectedUnit.getSelectedUpgrades().stream()
                .filter(SelectedUpgrade::isSelected)
                .toList();
        for (SelectedUpgrade upgrade : selectedUpgradeList) {
            switch (upgrade.getUpgrade().getName()) {
                case "Frenzy":
                    selectedStats.setA(selectedStats.getA() + 1);
                    break;
                case "Dodge (6+)":
                    selectedStats.setWardSave(6);
                    break;
                case "Armour Save(4+)":
                    selectedStats.setBasicSave(4);
                    break;
            }
        }
        selectedStatsRepository.save(selectedStats);
    }

    private int safeDecrement(Integer value, int dec, int defaultValue) {
        return (value == null ? defaultValue : value) - dec;
    }

    private int safeCalculate(Integer baseValue, int decValue) {
        if (baseValue == null) return 6;
        return baseValue - decValue;
    }

}
