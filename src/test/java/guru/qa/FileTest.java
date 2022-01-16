package guru.qa;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class FileTest {

    @Test
    public void downloadGradle () throws Exception {
        open("https://github.com/mipkasnow/QaGuru-HW7/blob/main/build.gradle");
        File downloadedFile = $("#raw-url").download();
        File localFile = new File("src/test/resources/gradle.txt");

        try(InputStream is = new FileInputStream(downloadedFile)) {
            assertThat(new String(is.readAllBytes(), StandardCharsets.UTF_8))
                    .contains("testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.2'");
        }

        try(InputStream is1 = new FileInputStream(downloadedFile); InputStream is2 = new FileInputStream(localFile)) {
            assertThat(new String(is1.readAllBytes(), StandardCharsets.UTF_8))
                    .contains(new String(is2.readAllBytes(), StandardCharsets.UTF_8));
        }
    }


}
