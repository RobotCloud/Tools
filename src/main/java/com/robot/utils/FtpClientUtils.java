package com.robot.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP工具类。
 *
 * @Author 张宝旭
 * @Date 2021/4/29
 */
public class FtpClientUtils {

    private FTPClient ftpClient;

    private FtpClientUtils(String host, int port, String username, String passwd) {
        this.ftpClient = new FTPClient();
        this.ftpClient.setControlEncoding("UTF-8");
        System.out.println(String.format("连接FTP服务器:%s,端口:%d,用户名:%s,密码:%s", host, port, username, passwd));
        try {
            this.ftpClient.connect(host, port);
            this.ftpClient.login(username, passwd);
            int replyCode = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.err.println("连接失败!返回码:" + replyCode);
            }
            System.out.println("连接成功!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化连接ftp服务器。
     *
     * @param host     主机地址
     * @param port     端口号
     * @param username 用户名
     * @param password 密码
     * @return ftp客户端
     */
    public static FtpClientUtils init(String host, int port, String username, String password) {
        return new FtpClientUtils(host, port, username, password);
    }

    /**
     * 上传文件到ftp。
     *
     * @param serverPath     ftp文件路径
     * @param serverFilename ftp文件名称
     * @param originFilePath 源文件路径
     * @return 是否上传成功
     */
    public boolean upload(String serverPath, String serverFilename, String originFilePath) {
        try (InputStream in = new FileInputStream(new File(originFilePath))) {
            return uploadInputStream(serverPath, serverFilename, in);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 上传文件到ftp。
     *
     * @param serverPath     ftp文件路径
     * @param serverFilename ftp文件名称
     * @param input          输入流
     * @return 是否上传成功
     */
    public boolean uploadInputStream(String serverPath, String serverFilename, InputStream input) throws IOException {
        if (!ftpClient.changeWorkingDirectory(serverPath)) {
            ftpClient.makeDirectory(serverPath);
            ftpClient.changeWorkingDirectory(serverPath);
        }
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setRemoteVerificationEnabled(false);
        return ftpClient.storeFile(serverFilename, input);
    }

    // 删除ftp指定目录下的所有文件
    public int deleteFtpFiles(String ftpDir) {
        int count = 0;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(ftpDir);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                String fileName = new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                if (file.isFile()) {
                    if (ftpClient.deleteFile(fileName)) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 读取ftp上的csv文件。
     *
     * @param ftpPath  ftp路径
     * @param fileName 文件名
     * @return 读取的数据，String[]中每个字符串，就是一行中的一个数据
     */
    public List<String[]> getFtpCsvFile(String ftpPath, String fileName) {
        List<String[]> list = new ArrayList<>();
        String[] ftpFile = getFtpFile(ftpPath, fileName);
        if (ftpFile == null) {
            return null;
        }
        for (String s : ftpFile) {
            list.add(s.split(","));
        }
        return list;
    }

    /**
     * 根据名称获取文件
     *
     * @param ftpPath  FTP服务器文件相对路径
     * @param fileName 文件名
     */
    public String[] getFtpFile(String ftpPath, String fileName) {
        if (ftpClient != null) {
            try {
                if (!ftpClient.changeWorkingDirectory(ftpPath)) {
                    return null;
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                InputStream inputStream = ftpClient.retrieveFileStream(fileName);//根据指定名称获取指定文件
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                StringBuilder stringBuilder = new StringBuilder(150);
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                ftpClient.completePendingCommand();
                String context = stringBuilder.toString();
                return context.replaceAll("\"", "").split("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 读取ftp上指定路径的所有csv文件。
     *
     * @param ftpPath ftp路径
     * @return 返回的数据
     */
    public List<List<String[]>> getAllFtpCsvFile(String ftpPath) {
        List<List<String[]>> list = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        if (ftpClient != null) {
            try {
                // 判断是否存在该目录
                if (!ftpClient.changeWorkingDirectory(ftpPath)) {
                    return null;
                }
                ftpClient.enterLocalPassiveMode();  // 设置被动模式，开通一个端口来传输数据
                FTPFile[] ftpFiles = ftpClient.listFiles();
                for (FTPFile ftpFile : ftpFiles) {
                    inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
                    inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    StringBuilder stringBuilder = new StringBuilder(150);
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    String context = stringBuilder.toString();
                    String[] split = context.replaceAll("\"", "").split("\n");
                    List<String[]> list1 = new ArrayList<>();
                    for (String s : split) {
                        list1.add(s.split(","));
                    }
                    list.add(list1);
                    ftpClient.completePendingCommand();
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 下载ftp指定文件，下载下来的文件名和ftp上的文件名相同。
     *
     * @param serverPath     ftp路径
     * @param serverFileName ftp文件名
     * @param localPath      本地路径
     * @return 是否成功
     */
    public boolean download(String serverPath, String serverFileName, String localPath) {
        return download(serverPath, serverFileName, localPath, serverFileName);
    }

    /**
     * 下载ftp指定文件。
     *
     * @param serverPath     ftp路径
     * @param serverFileName ftp文件名
     * @param localPath      本地路径
     * @param localFileName  本地文件名
     * @return 是否成功
     */
    public boolean download(String serverPath, String serverFileName, String localPath, String localFileName) {
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdir();
        }
        try (OutputStream out = new FileOutputStream(new File(localPath + File.separator + serverFileName))) {
            ftpClient.changeWorkingDirectory(serverPath);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setRemoteVerificationEnabled(false);
            boolean retrieveFile = ftpClient.retrieveFile(localFileName, out);
            if (retrieveFile) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 下载ftp指定目录的所有文件。
     *
     * @param serverPath ftp目录
     * @param localPath  要下载到的本地目录
     */
    public void downloadAllFile(String serverPath, String localPath) {
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdir();
        }
        OutputStream is = null;
        try {
            ftpClient.changeWorkingDirectory(serverPath);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setRemoteVerificationEnabled(false);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (!ftpFile.isFile()) {
                    continue;
                }
                String pathName = localPath + File.separator + ftpFile.getName();
                File localFile = new File(pathName);
                is = new FileOutputStream(localFile);
                ftpClient.retrieveFile(ftpFile.getName(), is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载ftp上名称包含指定关键字的所有文件。
     *
     * @param serverPath ftp路径
     * @param localPath  本地路径
     * @param keyword    名称关键字
     */
    public void downloadAllFile(String serverPath, String localPath, String keyword) {
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdir();
        }
        OutputStream is = null;
        try {
            ftpClient.changeWorkingDirectory(serverPath);// 转移到FTP服务器目录
            ftpClient.enterLocalPassiveMode();
            ftpClient.setRemoteVerificationEnabled(false);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (!ftpFile.isFile() || !ftpFile.getName().contains(keyword)) {
                    continue;
                }
                String pathName = localPath + File.separator + ftpFile.getName();
                File localFile = new File(pathName);
                is = new FileOutputStream(localFile);
                ftpClient.retrieveFile(ftpFile.getName(), is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            this.ftpClient.logout();
            this.ftpClient.disconnect();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


}
