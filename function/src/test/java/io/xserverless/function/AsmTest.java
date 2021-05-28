package io.xserverless.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.converter.FunctionConverter;
import io.xserverless.function.dto.XGroup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.LoggerFactory;

import static org.objectweb.asm.Opcodes.ASM9;

/*
 * a unit test shows basic usages of asm.
 */
public class AsmTest {
    @Test
    public void readClass() {
        try (InputStream inputStream = AsmTest.class.getResourceAsStream("/io/xserverless/function/AsmTest.class");
             PrintWriter printWriter = new PrintWriter(System.out)) {

            assert inputStream != null;

            ClassReader classReader = new ClassReader(inputStream);
            classReader.accept(new TraceClassVisitor(printWriter), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void showClassLoader() {
        System.out.println(String.class.getClassLoader());
        System.out.println(java.io.PrintWriter.class.getClassLoader());
        System.out.println(javax.swing.text.ComponentView.class.getClassLoader());
        System.out.println(AsmTest.class.getClassLoader());
    }

    @Test
    public void readSpringBootJar() throws IOException {
        String jarPath = "../../dev-center/target/dev-center-0.0.1-SNAPSHOT.jar";
        File jar = new File(jarPath);
        if (!jar.exists()) {
            System.out.println("run 'mvn package' in dev-center");
            return;
        }

        try (FileInputStream fis = new FileInputStream(jar)) {
            XGroup group = new FunctionConverter().readJar(fis);

            group.stream().forEach(System.out::println);
        }
    }


    // unzip jar
    @Test
    public void analysisSpringBootJarUnzipJar() throws IOException {
        String jarPath = "../../dev-center/target/dev-center-0.0.1-SNAPSHOT.jar";
        File jar = new File(jarPath);
        if (!jar.exists()) {
            System.out.println("run 'mvn package' in dev-center");
            return;
        }

        File tempRoot = new File("./target/tempJar");
        FileUtils.deleteDirectory(tempRoot);

        try (FileInputStream fis = new FileInputStream(jar)) {
            new FunctionConverter().unzip(fis, tempRoot);
        }
    }

    // read unzipped jar
    @Test
    public void analysisSpringBootJarReadJars() throws IOException {
        File tempJar = new File("./target/tempJar");
        XGroup group = new XGroup();

        System.out.println("read jar");

        new FunctionConverter().readJar(group, tempJar);

        System.out.println("output");

        File tempRoot = new File("./target/tempOutputFile");
        FileUtils.deleteDirectory(tempRoot);

        List<File> files = new ArrayList<>();
        new XFunction().analysis(entry -> {
            File file = new File(tempRoot, entry.getKey().getName() + "_" + files.size() + ".zip");
            files.add(file);
            try {
                FileUtils.writeByteArrayToFile(file, entry.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, FunctionFilter.REQUEST_MAPPING_FILTER, group);

        unzipJars(files);
    }


    @Test
    public void writeJava8Class() throws IOException {
        File funcClassFile = new File("./tempJar/BOOT-INF/classes/io/xserverless/devcenter/functions/service/FunctionService.class");
        File funcClassFileOutput = new File("./tempOutputFile/filtered/FunctionService.class");
        File funcClassFileOriginal = new File("./tempOutputFile/original/FunctionService.class");

        FileUtils.deleteDirectory(funcClassFileOutput.getParentFile());
        FileUtils.deleteDirectory(funcClassFileOriginal.getParentFile());

        funcClassFileOutput.getParentFile().mkdirs();
        funcClassFileOriginal.getParentFile().mkdirs();

        try (InputStream inputStream = new FileInputStream(funcClassFile);
             OutputStream outputStream = new FileOutputStream(funcClassFileOutput);
             OutputStream outputStreamOriginal = new FileOutputStream(funcClassFileOriginal);
        ) {
            CommandGroup.ClassCommandGroup commandGroup = ClassCommandReader.read(inputStream, ASM9);

            XGroup group = new FunctionConverter().convert(Collections.singletonList(commandGroup));

            CommandFilter commandFilter = CommandFilter.createFilter(group, group.createFunction("io/xserverless/devcenter/functions/service/FunctionService", "upload",
                    "(Lorg/springframework/web/multipart/MultipartFile;)V"));

            LoggerFactory.getLogger(getClass()).info("filtered class");
            byte[] bytes = new ClassCommandWriter().write(commandGroup, commandFilter);
            outputStream.write(bytes);
            LoggerFactory.getLogger(getClass()).info("original class");
            bytes = new ClassCommandWriter().write(commandGroup, CommandFilter.ALL);
            outputStreamOriginal.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void analysisSpringBootJar() throws IOException {
        String jarPath = "../../dev-center/target/dev-center-0.0.1-SNAPSHOT.jar";
        File jar = new File(jarPath);
        if (!jar.exists()) {
            System.out.println("run 'mvn package' in dev-center");
            return;
        }

        File tempRoot = new File("./target/tempFile");
        FileUtils.deleteDirectory(tempRoot);

        List<File> files = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(jar)) {
            new XFunction().analysis(fis, entry -> {
                File file = new File(tempRoot, entry.getKey().getName() + "_" + files.size() + ".zip");
                files.add(file);
                try {
                    FileUtils.writeByteArrayToFile(file, entry.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, FunctionFilter.REQUEST_MAPPING_FILTER);
        }

        unzipJars(files);
    }

    private void unzipJars(List<File> files) throws IOException {
        for (File f : files) {
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(f))) {
                while (true) {
                    ZipEntry zipEntry = zipInputStream.getNextEntry();
                    if (zipEntry == null) {
                        break;
                    }

                    File file = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 4), zipEntry.getName());
                    if (zipEntry.getName().endsWith("/")) {
                        file.mkdirs();
                        continue;
                    }
                    file.getParentFile().mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        IOUtils.copy(zipInputStream, fos);
                    }
                }
            }
            FileUtils.deleteQuietly(f);
        }
    }

    int classVariable = 0;

    static int staticVariable = 0;

    void callClassVariable() {
        classVariable++;
    }

    void callStaticVariable() {
        staticVariable++;
    }

    static int multipleReturn() {
        long random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("0")) {
            if (random > 0) {
                return 100;
            }
            return 0;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("1")) {
            if (random > 0) {
                return 100;
            }
            return 1;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("2")) {
            return 2;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("3")) {
            return 3;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("4")) {
            return 4;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("5")) {
            return 5;
        }
        random = System.currentTimeMillis();
        return -1;
    }
}
