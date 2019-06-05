package com.hellozjf.learn.corejava11.v1ch05.resources;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import javax.swing.*;

/**
 * @author Cay Horstmann
 * @version 1.5 2018-03-15
 */
public class ResourceTest {

    private static void createTitleTxt() {
        // File(".")，就是当前learn目录
        File folder = new File("./corejava11/target/classes/corejava");
        System.out.println(folder.getAbsolutePath());
        folder.mkdirs();
        System.out.println(folder.getAbsolutePath());
        File file = new File(folder, "title.txt");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
             PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
            printWriter.println("helloworld");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        // 资源只有放在resources目录才会被拷贝到classpath目录
        Class cl = ResourceTest.class;
        URL aboutURL = cl.getResource("about.gif");
        var icon = new ImageIcon(aboutURL);

        InputStream stream = cl.getResourceAsStream("data/about.txt");
        var about = new String(stream.readAllBytes(), "UTF-8");

        createTitleTxt();
        // classpath的/就是target/classes目录
        URL url = cl.getResource("/");
        System.out.println(url.getPath());
        InputStream stream2 = cl.getResourceAsStream("/corejava/title.txt");
        var title = new String(stream2.readAllBytes(), StandardCharsets.UTF_8).trim();

        JOptionPane.showMessageDialog(null, about, title, JOptionPane.INFORMATION_MESSAGE, icon);
    }
}
