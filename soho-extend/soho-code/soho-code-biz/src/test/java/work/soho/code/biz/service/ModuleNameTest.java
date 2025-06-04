package work.soho.code.biz.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.test.TestApp;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
public class ModuleNameTest {
    private static void scanJar(File jarFile, Set<String> packages) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace("/", ".").replace(".class", "");
                    int lastDot = className.lastIndexOf('.');
                    if (lastDot > 0) {
                        packages.add(className.substring(0, lastDot));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getAllPackages() {
        Set<String> packages = new HashSet<>();
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(System.getProperty("path.separator"));

        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                scanDirectory(file, "", packages);
            } else if (file.getName().endsWith(".jar")) {
                scanJar(file, packages);
            }
        }
        return packages;
    }

    private static void scanDirectory(File directory, String pkgName, Set<String> packages) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                String newPkgName = pkgName.isEmpty() ? file.getName() : pkgName + "." + file.getName();
                packages.add(newPkgName);
                scanDirectory(file, newPkgName, packages);
            }
        }
    }

    @Test
    void testGetAllPackages() {
        Set<String> packages = getAllPackages();
        for (String pkg : packages) {
            if(pkg.startsWith("work.soho") && pkg.endsWith(".biz")) {
                System.out.println(pkg);
                String name = pkg.replace( ".biz", "").replace("work.soho.", "");
                System.out.println( name);
            }
        }
    }

    @Test
    void testNames() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Package[] packages = classLoader.getDefinedPackages();
        for (Package pkg : packages) {
            if(pkg.getName().startsWith("work.soho")) {
                System.out.println(pkg.getName());
            }

        }
    }
}
