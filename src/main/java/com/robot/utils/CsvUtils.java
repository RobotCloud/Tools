package com.robot.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * csv文件处理工具。
 *
 * @Author 张宝旭
 * @Date 2021/4/7
 */
public class CsvUtils {

    /**
     * 读取CSV文件。
     *
     * @param localFile CSV文件
     * @return 列表中的每一个元素就是一个csv文件内容，里层list中的每一个元素就是csv的一行数据
     */
    public static List<String[]> transformCSV(String localFile) {
        String[] nextLine;
        List<String[]> list = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(localFile);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
             CSVReader reader = new CSVReader(inputStreamReader)) {
            while ((nextLine = reader.readNext()) != null) {
                list.add(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取文件异常", e);
        } catch (CsvValidationException e) {
            e.printStackTrace();
            throw new RuntimeException("读取CSV数据异常");
        }
        return list;
    }

    /**
     * 读取指定目录下，所有文件名称中包含指定关键字的csv文件。
     *
     * @param filePath 文件目录
     * @param keyword  关键字
     * @return 列表中的每一个元素就是一个csv文件内容，里层list中的每一个元素就是csv的一行数据
     */
    public static List<List<String[]>> transformAllCSV(String filePath, String keyword) {
        List<List<String[]>> resultList = new ArrayList<>();
        File file = new File(filePath);
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return resultList;
        }
        for (File listFile : listFiles) {
            List<String[]> list = new ArrayList<>();
            if (listFile.isFile() && listFile.getName().contains(keyword)) {
                String[] nextLine;
                try (FileInputStream fileInputStream = new FileInputStream(filePath + File.separator + listFile.getName());
                     InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                     CSVReader reader = new CSVReader(inputStreamReader)) {
                    while ((nextLine = reader.readNext()) != null) {
                        list.add(nextLine);
                    }
                    resultList.add(list);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("读取文件异常", e);
                } catch (CsvValidationException e) {
                    e.printStackTrace();
                    throw new RuntimeException("读取CSV数据异常");
                }
            }
        }
        return resultList;
    }


    /**
     * 创建csv文件。
     *
     * @param head 头部标题
     * @param dataList 数据，按行
     * @param outPutPath 输出路径
     * @param filename 文件名
     * @return 创建的文件
     */
    public static File createCSVFile(List<Object> head, List<List<Object>> dataList, String outPutPath, String filename) {
        File csvFile = null;
        BufferedWriter csvWriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();
            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8), 1024);
            writeRow(head, csvWriter);
            for (List<Object> row : dataList) {
                writeRow(row, csvWriter);
            }
            csvWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }


    /**
     * 创建csv数据。
     *
     * @param head 标题
     * @param dataList 数据
     * @return csv数据
     */
    public static String createCsvData(List<Object> head, List<List<Object>> dataList) {
        StringBuilder stringBuilder = new StringBuilder();
        String header = convertRow(head);
        stringBuilder.append(header).append("\n");
        for (int i = 0; i < dataList.size(); i++) {
            String line = convertRow(dataList.get(i));
            if (i != dataList.size() - 1) {
                stringBuilder.append(line).append("\n");
            } else {
                stringBuilder.append(line);
            }
        }
        return String.valueOf(stringBuilder);
    }

    // 写入一行csv内容
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        for (int i = 0; i < row.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            String rowStr;
            if (i == row.size() - 1) { // 最后一个没有逗号
                rowStr = stringBuilder.append(row.get(i)).toString();
            } else {
                rowStr = stringBuilder.append(row.get(i)).append(",").toString();
            }
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }

    // 将一行列表内容转换为csv
    private static String convertRow(List<Object> row) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            if (i == row.size() - 1) { // 最后一个没有逗号
                stringBuilder.append(row.get(i)).toString();
            } else {
                stringBuilder.append(row.get(i)).append(",").toString();
            }
        }
        return String.valueOf(stringBuilder);
    }
}
