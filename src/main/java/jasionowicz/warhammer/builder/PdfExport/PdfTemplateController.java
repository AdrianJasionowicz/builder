package jasionowicz.warhammer.builder.PdfExport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PdfTemplateController {

    private PdfTemplateService pdfTemplateService;

    public PdfTemplateController(PdfTemplateService pdfTemplateService) {
        this.pdfTemplateService = pdfTemplateService;
    }

        @PostMapping("/exportPdf")
        public ResponseEntity<byte[]> generateArmyPdf(@RequestBody PdfDataPreview pdfDataPreview) {
            try {
                byte[] pdfBytes = pdfTemplateService.generateArmyPdf(pdfDataPreview.getArmyName(), pdfDataPreview.getArmyId());
                return ResponseEntity.ok()
                        .header("Content-Disposition", "inline; filename=" + pdfDataPreview.getArmyName() + ".pdf")
                        .header("Content-Type", "application/pdf")
                        .body(pdfBytes);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(null);
            }
        }



}
