package com.company;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        String path = "T:\\Оплаты не передаю в январе загрузим в АСУ";
        File file = new File(path);
        if (file.listFiles() == null){
            System.out.println("Directory is empty");
            return;
        }

        oplType1(file);
        //oplType2(file);
    }

    private static void oplType2(File file) throws IOException{

        File[] files = file.listFiles();

        for(File f:files) {
            //Убрали директории
            if (!f.isFile()) continue;

            //убрали не текст
            if (Arrays.stream(f.getName().split("\\.")).noneMatch(p -> p.equalsIgnoreCase("txt"))) continue;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader( new FileInputStream(f.getPath()), "cp1252"));

            //Скипаем 12 строк
            for (int i = 0; i < 12; i++) {
                bufferedReader.readLine();
            }

            while(bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String[] splitLine = line.split(";");
                if ((splitLine.length != 11)||line.endsWith(";")) {
                    System.out.println(f.getName());
                    System.out.println(line);
                }
            }

            bufferedReader.close();

        }
    }

    private static void oplType1(File file) throws IOException {
        boolean badFile;
        boolean firstLine;
        File[] files = file.listFiles();

        for(File f:files) {
            //Убрали директории
            if (!f.isFile()) continue;

            //убрали не текст
            if (Arrays.stream(f.getName().split("\\.")).noneMatch(p -> p.equalsIgnoreCase("txt"))) continue;

            badFile = false;
            firstLine = true;
            //System.out.println(f.getName());


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader( new FileInputStream(f.getPath()), "cp1252"));
            BufferedWriter bufferedWriter = null;
            String newFileName = null;

            while(bufferedReader.ready()){
                String line = bufferedReader.readLine();

                String[] splitLine = line.split(";");

                if (splitLine.length == 18 && !badFile) continue;

                //= это контрольная сумма, она нам вообще не интересна
                if (line.startsWith("=")) continue;

                if (!badFile){
                    System.out.println(f.getName());
                    newFileName = f.getPath().substring(0, f.getPath().length() - 4) + "(2).txt";
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFileName), "cp1252"));
                    badFile = true;
                }
                if (firstLine){
                    firstLine = false;
                } else {
                    bufferedWriter.newLine();
                }

                //Если строка кривая - в 13 ячейку скопируем данные из предпоследней
                if (splitLine.length != 18){
                    String sum = splitLine[splitLine.length - 2];
                    line = "";
                    for (int i = 0; i < 18; i++) {

                        if (i > 0) line = line.concat(";");

                        if (i == 12) {
                            line = line.concat(sum);
                        } else if (i < splitLine.length) {
                            line = line.concat(splitLine[i]);
                        } else {
                            line = line.concat("*");
                        }
                    }
                }

                if (line.endsWith(";")) line = line.concat("*");

                bufferedWriter.write(line);

            }

            if (badFile){
                bufferedWriter.flush();
                bufferedWriter.close();
            }

            bufferedReader.close();

        }
    }
}
