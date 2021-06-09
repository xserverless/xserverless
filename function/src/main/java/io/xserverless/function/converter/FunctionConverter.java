package io.xserverless.function.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.dto.XGroup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import static org.objectweb.asm.Opcodes.ASM9;

public class FunctionConverter {
    public void getFunctions(CommandGroup.ClassCommandGroup commandGroup, XGroup group) {
        String owner = null;
        for (ClassCommand classCommand : commandGroup.getCommands()) {
            if (classCommand instanceof ClassCommand.Default) {
                owner = ((ClassCommand.Default) classCommand).getName();
            }

            classCommand.updateType(owner, group);
        }
        if (owner != null) {
            group.addClass(owner, commandGroup);
        }
    }

    public XGroup convert(List<CommandGroup.ClassCommandGroup> commandGroupList) {
        XGroup group = new XGroup();
        for (CommandGroup.ClassCommandGroup commandGroup : commandGroupList) {
            getFunctions(commandGroup, group);
        }
        return group;
    }

    public void unzip(InputStream inputStream, File tempPath)  throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            while (true) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) {
                    break;
                }

                File file = new File(tempPath, zipEntry.getName());
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
    }

    public XGroup readJar(InputStream inputStream) throws IOException {
        XGroup group = new XGroup();
        // unzip to temp path
        File tempPath = new File("./tempPath-" + System.currentTimeMillis());
        try {
            unzip(inputStream, tempPath);

            readJar(group, tempPath);
        } finally {
            FileUtils.deleteDirectory(tempPath);
        }

        return group;
    }

    public void readJar(XGroup group, File tempPath) throws IOException {
        Set<String> classSet = new HashSet<>();
        readClassPath(tempPath).forEach(entry -> {
            String name = entry.getKey();
            if (name.endsWith(".class")) {
                try {
                    readClass(entry.getValue(), group, classSet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (name.endsWith(".jar")) {
                readJar(entry.getValue(), group, classSet);
            }
        });
        group.updateInherits();
    }

    private void readClass(File file, XGroup group, Set<String> classSet) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            readClass(fileInputStream, group, classSet);
        }
    }

    private void readClass(InputStream inputStream, XGroup group, Set<String> classSet) {
        CommandGroup.ClassCommandGroup commandGroup = ClassCommandReader.read(inputStream, ASM9);
        Optional<ClassCommand> first = commandGroup.getCommands().stream().filter(classCommand -> classCommand instanceof ClassCommand.Default).findFirst();
        if (first.isPresent()) {
            String name = ((ClassCommand.Default) first.get()).getName();
            if (classSet.contains(name)) {
                return;
            }
            classSet.add(name);
        }

        getFunctions(commandGroup, group);
    }

    private List<Map.Entry<String, File>> readClassPath(File path) throws IOException {
        List<Map.Entry<String, File>> list = new ArrayList<>();

        // manifest
        Map<String, String> manifestMap = new HashMap<>();
        try (FileInputStream fileInputStream = new FileInputStream(new File(path, "META-INF/MANIFEST.MF"))) {
            Manifest manifest = new Manifest(fileInputStream);
            manifest.getMainAttributes().forEach((k, v) -> {
                manifestMap.put(String.valueOf(k), String.valueOf(v));
            });
        }

        // classes
        if (manifestMap.containsKey("Spring-Boot-Classes")) {
            File classPath = new File(path, manifestMap.get("Spring-Boot-Classes"));
            int length = classPath.getAbsolutePath().length();
            Iterator<File> fileIterator = FileUtils.iterateFiles(classPath, new String[]{"class"}, true);
            fileIterator.forEachRemaining(file -> {
                list.add(new AbstractMap.SimpleEntry<>(file.getAbsolutePath().substring(length), file));
            });
        }

        // jars
        if (manifestMap.containsKey("Spring-Boot-Classpath-Index")) {
            File classPathIndexFile = new File(path, manifestMap.get("Spring-Boot-Classpath-Index"));
            FileUtils.lineIterator(classPathIndexFile).forEachRemaining(s -> {
                String name = s.replace("- \"", "").replace("\"", "");
                list.add(new AbstractMap.SimpleEntry<>(name, new File(path, name)));
            });
        }

        return list;
    }

    private void readJar(File file, XGroup group, Set<String> classSet) {
        try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(file))) {
            while (true) {
                JarEntry jarEntry = jarInputStream.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if (!jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                readClass(jarInputStream, group, classSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
