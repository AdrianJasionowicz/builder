package jasionowicz.warhammer.builder.Mapper;

import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface SelectedUnitMapper {
    @Mapping(target = "name", expression = "java(selectedUnit.getUnit() != null ? selectedUnit.getUnit().getName() : \"Brak nazwy\")")
    SelectedUnitDTO selectedUnitToSelectedUnitDTO(SelectedUnit selectedUnit);
    @Mapping(target = "unit", ignore = true)
    SelectedUnit selectedUnitDToToSelectedUnit(SelectedUnitDTO selectedUnitDTO);
}
