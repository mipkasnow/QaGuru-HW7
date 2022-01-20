package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class FileTest {

    private ClassLoader cl = FileTest.class.getClassLoader();

    // Проверка загруженного txt файла
    @Test
    public void downloadGradle () throws Exception {
        open("https://github.com/mipkasnow/QaGuru-HW7/blob/main/build.gradle");
        File downloadedFile = $("#raw-url").download();
        File localFile = new File("src/test/resources/gradle.txt");

        // Проверяем наличие интересующей строчки в файле
        try(InputStream is = new FileInputStream(downloadedFile)) {
            assertThat(new String(is.readAllBytes(), StandardCharsets.UTF_8))
                    .contains("testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.2'");
        }
        // Сравниваем файл, который скачали, с другим файлом из ресурсов
        try(InputStream is1 = new FileInputStream(downloadedFile); InputStream is2 = new FileInputStream(localFile)) {
            assertThat(new String(is1.readAllBytes(), StandardCharsets.UTF_8))
                    .contains(new String(is2.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    // Отдельный тест на pdf
    @Test
    public void pdfTest() throws Exception{
        File pdfFile = new File("src/test/resources/pdf.pdf");
        PDF parsed = new PDF(pdfFile);
        String text = parsed.text;
        System.out.println(text);
        assertThat(text).contains("Сертификат: E7E3E0D511C825E499BE6695B711D421890E1D09");
    }

    // Отдельный тест на xls
    @Test
    public void xlsTest() throws Exception{
        try(InputStream stream = cl.getResourceAsStream("xls.xls")){
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()).
                    contains("Тип (товар/ услуга)");

        }
    }

    // Отдельный тест на csv
    @Test
    public void csvTest() throws Exception{
        try(InputStream stream = cl.getResourceAsStream("csv2.csv")){
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();
            assertThat(list)
                    .hasSize(3)
                    .contains(
                            new String[] {"name", "lastname", "age"},
                            new String[] {"Misha", "Loginov", "29"},
                            new String[] {"Masha", "Petrova", "34"}
                    );
        }
    }

    // Домашняя работа
    @Test
    public void zipTest() throws Exception{

        //Проверяем имена файлов в архиве
        try(InputStream stream = cl.getResourceAsStream("zip.zip"); ZipInputStream zis = new ZipInputStream(stream) ){
            ZipEntry zipEntry;
            while((zipEntry = zis.getNextEntry()) != null){
                System.out.println(zipEntry.getName());
            }

        }

        ZipFile zipFile = new ZipFile("src/test/resources/zip.zip");

        // Проверка xls
        ZipEntry XlsEntry = zipFile.getEntry("xls.xls");
        try (InputStream stream = zipFile.getInputStream(XlsEntry)) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()).
                    contains("Тип (товар/ услуга)");
        }

        // Проверка csv
        ZipEntry csvEntry = zipFile.getEntry("csv2.csv");
        try (InputStream stream = zipFile.getInputStream(csvEntry)) {
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();
            assertThat(list)
                    .hasSize(3)
                    .contains(
                            new String[] {"name", "lastname", "age"},
                            new String[] {"Misha", "Loginov", "29"},
                            new String[] {"Masha", "Petrova", "34"}
                    );
        }

        //Проверка pdf
        ZipEntry pdfEntry = zipFile.getEntry("pdf.pdf");
        try (InputStream stream = zipFile.getInputStream(pdfEntry)) {
            PDF parsed = new PDF(stream);
            assertThat(parsed.text).contains("Сертификат: E7E3E0D511C825E499BE6695B711D421890E1D09");
        }
    }

}
