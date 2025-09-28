package jasionowicz.warhammer.builder.PdfExport;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jasionowicz.warhammer.builder.Army.Army;
import jasionowicz.warhammer.builder.Army.ArmyRepository;
import jasionowicz.warhammer.builder.Army.ArmyService;
import jasionowicz.warhammer.builder.SelectedStats.SelectedStatsService;
import jasionowicz.warhammer.builder.SelectedUnit.SelectedUnit;
import jasionowicz.warhammer.builder.SelectedUpgrade.SelectedUpgrade;
import jasionowicz.warhammer.builder.Unit.Unit;
import jasionowicz.warhammer.builder.Unit.UnitRepository;
import jasionowicz.warhammer.builder.UnitStats.UnitStats;
import jasionowicz.warhammer.builder.UnitStats.UnitStatsRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PdfTemplateService {


    private final ArmyService armyService;
    private final UnitRepository unitRepository;
    private final UnitStatsRepository unitStatsRepository;
    private ArmyRepository armyRepository;
    private SelectedStatsService selectedStatsService;

    public PdfTemplateService(ArmyService armyService, UnitRepository unitRepository, UnitStatsRepository unitStatsRepository, ArmyRepository armyRepository, SelectedStatsService selectedStatsService) {
        this.armyService = armyService;
        this.unitRepository = unitRepository;
        this.unitStatsRepository = unitStatsRepository;
        this.armyRepository = armyRepository;
        this.selectedStatsService = selectedStatsService;
    }


    public byte[] generateArmyPdf(String armyName, Long armyId) {
        selectedStatsService.adjustArmyStatsToSelectedUpgrades(armyId);
        Army army = armyRepository.getReferenceById(armyId);


        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Warhammer Army: " + armyName).setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Army: " + army.getFactionName() + " " + army.getPointsUsed() + " / "  + army.getPointsLimit() + " pts").setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph().setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Is Army valid: " + armyService.isArmyValid(armyId)).setBold().setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            List<SelectedUnit> selectedUnitList = army.getSelectedUnitsList();

            for (int i = 0; i < 5; i++) {
                String armyType = switch (i) {
                    case 0 -> "Lords";
                    case 1 -> "Hero";
                    case 2 -> "Core";
                    case 3 -> "Special";
                    case 4 -> "Rare";
                    default -> null;
                };
                List<SelectedUnit> selectedUnitList1 = selectedUnitList.stream().filter(unit -> unit.getUnitType().equals(armyType)).toList();

                Table table = new Table(1).setWidth(UnitValue.createPercentValue(100));
                Cell cell = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY);
                Paragraph p = new Paragraph();
                p.addTabStops(new TabStop(520, TabAlignment.RIGHT));
                p.add(armyType + ": ").setBold();
                p.add(new Tab());
                switch (armyType) {
                    case "Lords":
                        p.add(String.valueOf(army.getLordPointsUsed() + " pts"));
                        break;
                    case "Hero":
                        p.add(String.valueOf(army.getHeroPointsUsed() + " pts"));
                        break;
                    case "Core":
                        p.add(String.valueOf(army.getCorePointsUsed() + " pts"));
                        break;
                    case "Special":
                        p.add(String.valueOf(army.getSpecialPointsUsed() + " pts"));
                        break;
                    case "Rare":
                        p.add(String.valueOf(army.getRarePointsUsed() + " pts"));
                        break;
                }
                cell.add(p);
                table.addCell(cell);
                document.add(table);

                for (SelectedUnit selectedUnitLists : selectedUnitList1) {                             //
                    Table table2 = new Table(1).setWidth(UnitValue.createPercentValue(100));
                    Paragraph p2 = new Paragraph();
                    p2.addTabStops(new TabStop(1000, TabAlignment.RIGHT));
                    p2.add(new Text(selectedUnitLists.getUnit().getName() + " Squad"));                      //
                    p2.add(new com.itextpdf.layout.element.Tab());
                    p2.add(new Text(String.valueOf(selectedUnitLists.getTotalCost()) + " pts"));             //

                    Cell cell2 = new Cell().add(p2);
                    table2.addCell(cell2);
                    document.add(table2);

                    Table statsTable = new Table(13).setWidth(UnitValue.createPercentValue(100));
                    String[] headers = {"Unit", "Qty", "M", "Ws", "Bs", "S", "T", "W", "I", "A", "Ld", "Save", "Ward Save"};
                    for (String header : headers) {
                        statsTable.addCell(
                                new Cell().add(new Paragraph(header))
                                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                        .setTextAlignment(TextAlignment.CENTER)

                        );
                    }
                    // Stats
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getUnit().getName()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int) Math.round(selectedUnitLists.getQuantity())))).setTextAlignment(TextAlignment.CENTER));
                    if (selectedUnitLists.getUnit().getName().equalsIgnoreCase("Hell-pit Abomination") || selectedUnitLists.getUnit().getName().equalsIgnoreCase("Doomwheel")) {
                        statsTable.addCell(new Cell().add(new Paragraph(("3D6")))).setTextAlignment(TextAlignment.CENTER);
                    } else {
                        statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getM()))).setTextAlignment(TextAlignment.CENTER));
                    }
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getWs()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getBs()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getS()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getT()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getW()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getI()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getA()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getLd()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                    statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(selectedUnitLists.getSelectedStats().getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                    //Upg
                    List<SelectedUpgrade> selectedUpgradeList = selectedUnitLists.getSelectedUpgrades().stream()
                            .filter(SelectedUpgrade::isSelected)
                            .toList();
                    for (SelectedUpgrade selectedUpgrade : selectedUpgradeList) {
                        String type = selectedUpgrade.getUpgrade().getUpgradeType();

                        if (type.equalsIgnoreCase("Mount") || type.equalsIgnoreCase("Crew") || type.equalsIgnoreCase("Champion")) {
                            Unit unit = unitRepository.findByName(selectedUpgrade.getUpgrade().getName());
                            UnitStats stats = unitStatsRepository.getReferenceById(unit.getUnitStats().getId());
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit.getName())))).setTextAlignment(TextAlignment.CENTER);
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int) Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getM()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBs()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getS()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getT()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getW()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getI()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getA()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getLd()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                            statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                            Unit unit1 = unitRepository.findByName(selectedUpgrade.getUpgrade().getName() + " Crew");
                            if (unit1 != null) {
                                UnitStats stats2 = unitStatsRepository.getReferenceById(unit1.getUnitStats().getId());
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(unit1.getNation())))).setTextAlignment(TextAlignment.CENTER);
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf((int) Math.round(unit.getMinQuantity())))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getM()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWs()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBs()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getS()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getT()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getW()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getI()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getA()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getLd()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getBasicSave()))).setTextAlignment(TextAlignment.CENTER));
                                statsTable.addCell(new Cell().add(new Paragraph(String.valueOf(stats2.getWardSave()))).setTextAlignment(TextAlignment.CENTER));
                            }


                        }

                    }
                    document.add(statsTable);


                    Table upgradesTable = new Table(3).setWidth(UnitValue.createPercentValue(100));

                    for (int z = 0; z < selectedUpgradeList.size(); z += 3) {

                        Cell cell9 = new Cell().add(new Paragraph(
                                selectedUpgradeList.get(z).getUpgrade().getName() +
                                        " (" + selectedUpgradeList.get(z).getUpgrade().getPointsCost() * selectedUpgradeList.get(z).getQuantity() + " pkt)"
                        )).setBorder(Border.NO_BORDER);
                        upgradesTable.addCell(cell9);

                        if (z + 1 < selectedUpgradeList.size()) {
                            Cell cell10 = new Cell().add(new Paragraph(
                                    selectedUpgradeList.get(z + 1).getUpgrade().getName() +
                                            " (" + selectedUpgradeList.get(z + 1).getUpgrade().getPointsCost() * selectedUpgradeList.get(z + 1).getQuantity() + " pkt)"
                            )).setBorder(Border.NO_BORDER);
                            upgradesTable.addCell(cell10);
                        } else {
                            upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                        }

                        if (z + 2 < selectedUpgradeList.size()) {
                            Cell cell11 = new Cell().add(new Paragraph(
                                    selectedUpgradeList.get(z + 2).getUpgrade().getName() +
                                            " (" + selectedUpgradeList.get(z + 2).getUpgrade().getPointsCost() * selectedUpgradeList.get(z + 2).getQuantity() + " pkt)"
                            )).setBorder(Border.NO_BORDER);
                            upgradesTable.addCell(cell11);
                        } else {
                            upgradesTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                        }
                    }


                    document.add(new Paragraph("Upgrades:").setFontSize(10).setMarginTop(10));
                    document.add(upgradesTable);


                }

            }
            Table upgradesNewTable = getUpgradesTable(selectedUnitList);
            document.add(upgradesNewTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Błąd przy generowaniu PDF: " + e.getMessage(), e);
        }
    }


    public Table getUpgradesTable(List<SelectedUnit> selectedUnitList) {
        Table upgradesNewTable = new Table(2).setWidth(UnitValue.createPercentValue(100));
        Set<String> added = new HashSet<>();

        selectedUnitList.stream()
                .flatMap(unit -> unit.getSelectedUpgrades().stream())
                .filter(SelectedUpgrade::isSelected)
                .map(SelectedUpgrade::getUpgrade)
                .filter(upgrade -> added.add(upgrade.getName()))
                .forEach(upgrade -> {
                    upgradesNewTable.addCell(new Cell().add(new Paragraph(upgrade.getName())));
                    upgradesNewTable.addCell(new Cell().add(new Paragraph(upgrade.getDescription())));
                });
        return upgradesNewTable;
    }
}




