package jasionowicz.warhammer.builder.Mapper;

import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface SelectedUnitMapper {

    SelectedUnitDTO selectedUnitToSelectedUnitDTO(SelectedUnit selectedUnit);
    SelectedUnit selectedUnitDToToSelectedUnit(SelectedUnitDTO selectedUnitDTO);
}
