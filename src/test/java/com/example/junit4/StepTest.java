package com.example.junit4;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.IntStream;

public class StepTest {

    private static final String GLOBAL_PARAMETER = "global value";
    private static final int LARGE_FILE_SIZE_MB = 10;
    private static final int NUM_FILES_PER_TEST = 10;

    @Test
    public void annotatedStepTest() throws IOException {
        annotatedStep("local value");
        generateLargeAttachments();
    }

    @Test
    public void lambdaStepTest() throws IOException {
        final String localParameter = "parameter value";
        Allure.step(String.format("Parent lambda step with parameter [%s]", localParameter), (step) -> {
            step.parameter("parameter", localParameter);
            Allure.step(String.format("Nested lambda step with global parameter [%s]", GLOBAL_PARAMETER));
            generateLargeAttachments();
        });
    }

    @Step("Parent annotated step with parameter [{parameter}]")
    public void annotatedStep(final String parameter) throws IOException {
        nestedAnnotatedStep();
    }

    @Step("Nested annotated step with global parameter [{this.GLOBAL_PARAMETER}]")
    public void nestedAnnotatedStep() throws IOException {
        generateLargeAttachments();
    }

    /**
     * Генерирует большие файлы и прикрепляет их к Allure-отчету.
     */
    private void generateLargeAttachments() throws IOException {
        IntStream.range(0, NUM_FILES_PER_TEST).forEach(i -> {
            try {
                String fileName = "large_file_" + System.currentTimeMillis() + "_" + i + ".bin";
                Path filePath = Paths.get("target/allure-results", fileName);

                byte[] data = new byte[1024 * 1024 * LARGE_FILE_SIZE_MB]; // 500 МБ
                new Random().nextBytes(data);
                Files.write(filePath, data);

                Allure.addAttachment(
                        "Large Attachment " + i,
                        "application/octet-stream",
                        Files.newInputStream(filePath),
                        ".bin"
                );

                // Логируем информацию о файле
                Allure.step("Generated large attachment: " + fileName + " (" + LARGE_FILE_SIZE_MB + " MB)");
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate large attachment", e);
            }
        });
    }
}