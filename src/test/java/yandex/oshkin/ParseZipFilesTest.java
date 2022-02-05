package yandex.oshkin;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

public class ParseZipFilesTest {


    @Test
    void parseZipFilesTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/files/files.zip");
        ZipEntry CsvEntry = zipFile.getEntry("cities.csv");
        ZipEntry XlsEntry = zipFile.getEntry("file_example_XLSX_50.xlsx");
        ZipEntry PdfEntry = zipFile.getEntry("sample.pdf");

        try (InputStream stream = zipFile.getInputStream(PdfEntry)) {
            PDF parsed = new PDF(stream);
            assertThat(parsed.producer).contains("Nevrona Designs");
            assertThat(parsed.text).contains("This is a small demonstration .pdf file");
        }

        try (InputStream stream = zipFile.getInputStream(CsvEntry)) {
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();
            assertThat(list).hasSize(130).contains(
                    new String[]{"37", "41", "23", "N", "97", "20", "23", "W", "Wichita", "KS"},
                    new String[]{"40", "4", "11", "N", "80", "43", "12", "W", "Wheeling", "WV"},
                    new String[]{"41", "25", "11", "N", "122", "23", "23", "W", "Weed", "CA"});
        }

        try (InputStream stream = zipFile.getInputStream(XlsEntry)) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(0).getRow(3).getCell(2).getStringCellValue())
                    .isEqualTo("Gent");
        }
    }

}
