package com.githab.smirnovdm2107.extractor;

import com.githab.smirnovdm2107.util.StringUtil;
import com.github.smirnovdm2107.extractor.DefaultExtractor;
import com.github.smirnovdm2107.extractor.Extractor;
import com.github.smirnovdm2107.extractor.ExtractorException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class DefaultExtractorTest {
    private static Path testDir;

    private static final int TEST_FILE_NAME_LENGTH = 10;

    private static Supplier<Extractor> extractorSupplier;
    private static Random random = new Random();
    private static final byte[] space = " ".getBytes(StringUtil.STANDARD_CHARSET);


    @BeforeAll
    public static void setUpTestDirectory() throws IOException {
        testDir = Files.createTempDirectory("extractor_test");
    }

    @BeforeAll
    public static void setUpExtractorSupplier() {
        extractorSupplier = DefaultExtractor::new;
    }

    public void test(final Path pathToTestFile, final List<URI> expectedResult) {
        final List<URI> real;
        try {
            real = extractorSupplier.get().extractLinks(pathToTestFile);
        } catch (final ExtractorException e) {
            Assertions.fail();
        }
        Assertions.assertNotNull(real);
        Assertions.assertEquals(real.size(), expectedResult.size());
        final Set<URI> distinct = new HashSet<>(real);
        Assertions.assertEquals(real.size(), distinct.size(), "Result contains duplicate");
        final Set<URI> merge = new HashSet<>(real);
        merge.addAll(expectedResult);
        Assertions.assertEquals(real.size(), merge.size());
    }

    @FunctionalInterface
    private interface CheckedConsumer<T> {
        void accept(T t) throws IOException;
    }

    private Path write(CheckedConsumer<OutputStream> consumer) {
        final Path testFilePath = testDir.resolve(StringUtil.generateRandomWord(TEST_FILE_NAME_LENGTH));
        try(final OutputStream out = Files.newOutputStream(testFilePath)) {
            consumer.accept(out);
        } catch (final IOException e) {
           Assertions.fail();
        }
        return testFilePath;
    }
    public void testSequence(final List<URI> expectedResult) {
        final Path testFilePath = write(out -> {
            for (final URI uri: expectedResult) {
                out.write(uri.toString().getBytes(StringUtil.STANDARD_CHARSET));
                out.write(System.lineSeparator().getBytes(StringUtil.STANDARD_CHARSET));
            }
        });
        test(testFilePath, expectedResult);
    }

    public void testRandom(final List<URI> expectedResult) {
        final byte[] space = " ".getBytes(StringUtil.STANDARD_CHARSET);
        final Path testFilePath = write((out) -> {
            for (final URI uri: expectedResult) {
                final byte[] bytes = new byte[random.nextInt(1024)];
                random.nextBytes(bytes);
                out.write(bytes);
                out.write(space);
                out.write(uri.toString().getBytes(StringUtil.STANDARD_CHARSET));
                out.write(space);
            }
            final byte[] bytes = new byte[random.nextInt(1024)];
            random.nextBytes(bytes);
            out.write(bytes);
        });
        test(testFilePath, expectedResult);
    }

    public void testString(final String string, final List<URI> expectedResult) {
        final Path testFilePath = write(out -> out.write(string.getBytes(Charset.defaultCharset())));
        test(testFilePath, expectedResult);
    }

    public void test(final List<URI> expectedResult) {
        testSequence(expectedResult);
        testRandom(expectedResult);
    }

    public void test(final int count) {
        testRandom(IntStream.range(0, count).mapToObj(it -> URI.create(StringUtil.generateURL())).toList());
    }
    @Test
    public void test1_emptyFile() {
        testSequence(List.of());
    }

    @AfterAll
    public static void tearDownTestDirectory() throws IOException {
        Files.walkFileTree(testDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
                throw new IOException("Can't delete file");
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
