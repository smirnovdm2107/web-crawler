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
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class DefaultExtractorTest {
    private static Path testDir;

    private static final int TEST_FILE_NAME_LENGTH = 10;

    private static Supplier<Extractor> extractorSupplier;
    private static final     Random random = new Random();
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
            e.printStackTrace();
            Assertions.fail();
            return;
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

    public void testSequence(final List<String> inserted, final List<URI> expectedResult) {
        Collections.shuffle(inserted);
        final Path testFilePath = write(out -> {
            for (final String uri: inserted) {
                out.write(uri.getBytes(StringUtil.STANDARD_CHARSET));
                out.write(System.lineSeparator().getBytes(StringUtil.STANDARD_CHARSET));
            }
        });
        test(testFilePath, expectedResult);
    }
    public void testSequence(final List<URI> expectedResult) {
        testSequence(expectedResult.stream().map(URI::toString).collect(Collectors.toCollection(ArrayList::new)), expectedResult);
    }

    public void testRandom(final List<String> inserted, final List<URI> expectedResult) {
        Collections.shuffle(inserted);
        final Path testFilePath = write((out) -> {
            for (final String uri: inserted) {
                final byte[] bytes = StringUtil.generateRandomWord(1024).getBytes(StringUtil.STANDARD_CHARSET);
                out.write(bytes);
                out.write(space);
                out.write(uri.getBytes(StringUtil.STANDARD_CHARSET));
                out.write(space);
            }
            final byte[] bytes = StringUtil.generateRandomWord(1).getBytes(StringUtil.STANDARD_CHARSET);
            out.write(bytes);
        });
        test(testFilePath, expectedResult);
    }

    public void testRandom(final List<URI> expectedResult) {
        testRandom(expectedResult.stream().map(URI::toString).collect(Collectors.toCollection(ArrayList::new)), expectedResult);
    }

    public void testString(final String string, final List<URI> expectedResult) {
        final Path testFilePath = write(out -> out.write(string.getBytes(Charset.defaultCharset())));
        test(testFilePath, expectedResult);
    }


    private List<URI> generateURIs(final int count) {
        return IntStream.range(0, count).mapToObj(it -> URI.create(StringUtil.generateURL())).toList();
    }

    public void test(final int count) {
        testRandom(generateURIs(count));
    }
    @Test
    public void test1_emptyFile() {
        testSequence(List.of());
    }

    @Test
    public void test2_sequence() {
        testSequence(IntStream.range(0, 5)
                .mapToObj(it -> URI.create(StringUtil.generateURL()))
                .toList());
    }

    private String glueURIs() {
        return generateURIs(1).get(0).toString() + "\"glue\"";
    }
    @Test
    public void test3_together() {
        final List<String> single = List.of(glueURIs());
        testSequence(single, List.of());
        testRandom(single, List.of());

        final List<String> several = IntStream.range(0, 10).mapToObj(it -> glueURIs()).collect(Collectors.toCollection(ArrayList::new));
        testSequence(several, List.of());
        testRandom(several, List.of());
    }

    @Test
    public void test4_togetherMixed() {
        final List<URI> expected = new ArrayList<>();
        final List<String> list = IntStream.range(0, 100).mapToObj(it -> {
            if (Math.random() > 0.5) {
                final URI uri = generateURIs(1).get(0);
                expected.add(uri);
                return uri.toString();
            } else {
                return glueURIs();
            }
        }).collect(Collectors.toCollection(ArrayList::new));
       testRandom(list, expected);
    }
    @Test
    public void test5_random() {
        IntStream.range(0, 10).forEach(it -> test(1000));
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
