package yandex.oshkin;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesTest {
    private ClassLoader cl = FilesTest.class.getClassLoader();

    @Test
    void parsePdfTest() throws Exception {
        Selenide.open("https://junit.org/junit5/docs/current/user-guide/");
        File pdfDownload = Selenide.$(byText("PDF download")).download();
        PDF parsed = new PDF(pdfDownload);
        assertThat(parsed.author).contains("Marc Philipp");
    }

    @Test
    void parseXlsTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("files/file_example_XLSX_50.xlsx")) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(0).getRow(3).getCell(2).getStringCellValue())
                    .isEqualTo("Gent");
        }
    }

    @Test
    void parseCsvFile() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("files/cities.csv")) {
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();
            assertThat(list).hasSize(130).contains(
                    new String[]{"37", "41", "23", "N", "97", "20", "23", "W", "Wichita", "KS"},
                    new String[]{"40", "4", "11", "N", "80", "43", "12", "W", "Wheeling", "WV"},
                    new String[]{"41", "25", "11", "N", "122", "23", "23", "W", "Weed", "CA"});
        }
    }

    @Test
    void zipTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("files/cities.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                assertThat(zipEntry.getName()).isEqualTo("cities.csv");
            }
        }
    }
}
