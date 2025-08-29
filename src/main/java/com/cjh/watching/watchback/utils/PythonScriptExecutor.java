package com.cjh.watching.watchback.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Python脚本执行器
 * 用于在Java中调用Python脚本
 * 
 * @author Cjh
 * @date 2025/8/29
 */
@Component
public class PythonScriptExecutor {


    /**
     * 执行Python脚本（编码安全版本）
     * 
     * @param scriptPath Python脚本的相对路径（相对于项目根目录）
     * @param args 传递给Python脚本的参数
     * @param timeoutSeconds 超时时间（秒）
     * @return 脚本执行结果
     * @throws Exception 执行异常
     */
    public String executePythonScriptSafe(String scriptPath, String[] args, int timeoutSeconds) throws Exception {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        String fullScriptPath = projectRoot + File.separator + scriptPath;
        
        // 检查脚本文件是否存在
        File scriptFile = new File(fullScriptPath);
        if (!scriptFile.exists()) {
            throw new IllegalArgumentException("Python脚本文件不存在: " + fullScriptPath);
        }

        // 构建命令，使用-u参数确保输出不被缓冲，并设置环境变量
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (args != null && args.length > 0) {
            String[] command = new String[args.length + 3];
            command[0] = "python";
            command[1] = "-u";  // 无缓冲输出
            command[2] = fullScriptPath;
            System.arraycopy(args, 0, command, 3, args.length);
            processBuilder.command(command);
        } else {
            processBuilder.command("python", "-u", fullScriptPath);
        }

        // 设置工作目录
        processBuilder.directory(new File(projectRoot));
        
        // 设置环境变量，强制使用UTF-8编码
        processBuilder.environment().put("PYTHONIOENCODING", "utf-8");
        processBuilder.environment().put("PYTHONLEGACYWINDOWSSTDIO", "utf-8");
        
        // 不合并错误流，分别处理
        processBuilder.redirectErrorStream(false);

        try {
            // 启动进程
            Process process = processBuilder.start();
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();
            
            // 使用线程分别读取标准输出和错误输出
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                } catch (Exception e) {
                    // 忽略读取异常
                }
            });
            
            Thread errorThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                } catch (Exception e) {
                    // 忽略读取异常
                }
            });
            
            outputThread.start();
            errorThread.start();

            // 等待进程完成
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("Python脚本执行超时");
            }
            
            // 等待线程完成
            outputThread.join(1000);
            errorThread.join(1000);

            // 检查退出码
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                String errorMessage = "Python脚本执行失败，退出码: " + exitCode;
                if (errorOutput.length() > 0) {
                    errorMessage += "\n错误输出: " + errorOutput.toString().trim();
                }
                if (output.length() > 0) {
                    errorMessage += "\n标准输出: " + output.toString().trim();
                }
                throw new RuntimeException(errorMessage);
            }

            return output.toString().trim();

        } catch (Exception e) {
            throw new Exception("执行Python脚本时发生错误: " + e.getMessage(), e);
        }
    }
}