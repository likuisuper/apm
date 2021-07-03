package com.cxylk.agent.output;


import com.cxylk.agent.IOutput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;


/**
 * @author cxylk
 */
public class SimpleOutput implements IOutput {
    private FileWriter fileWriter;


    public SimpleOutput(Properties properties) {
        try {
            fileWriter =
                    new FileWriter(openFile(properties.getProperty("log")), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean out(Object value) {
        try {
            fileWriter.write( value.toString() +"\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private File openFile(String rootDir) {
        try {
            //如果VM参数没有添加log=xx，那么就将日志文件输入到当前dir的logs目录下
            if (rootDir == null || rootDir.trim().equals("")) {
                rootDir = System.getProperty("user.dir") + "/logs/";
            }
            File root = new File(rootDir);
            if (!root.exists() || !root.isDirectory()) {
                root.mkdirs();
            }
            File file = new File(root, "apm-agent.log");
            if (file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
