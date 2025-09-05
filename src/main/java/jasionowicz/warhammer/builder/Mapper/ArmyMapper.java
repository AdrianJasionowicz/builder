package jasionowicz.warhammer.builder.Mapper;

import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyDTO;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArmyMapper {
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "selectedUnitsList", target = "selectedUnitsList")
    ArmyDTO armyToArmyDTO(Army army);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "selectedUnitsList", ignore = true)
    Army armyDToToArmy(ArmyDTO armyDTO);

}
