package com.cjh.watching.watchback.xxlJob;

import com.cjh.watching.watchback.utils.PythonScriptExecutor;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * - @author Cjh。
 * - @date 2025/8/29 15:36。
 **/
@Component
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);
    
    @Autowired
    private PythonScriptExecutor pythonScriptExecutor;
    
    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("upcomingXxlJob")
    public void upcomingXxlJob() throws Exception {
        XxlJobHelper.log("开始执行即将上映电影数据同步任务");
        logger.info("开始执行即将上映电影数据同步任务");
        
        try {
            // 调用Python脚本执行数据同步
            String result = pythonScriptExecutor.executePythonScriptSafe("py/upComingMovie.py", null, 300);
            
            // 记录执行结果
            XxlJobHelper.log("Python脚本执行完成，结果: " + result);
            logger.info("Python脚本执行完成，结果: {}", result);
            
            // 检查执行结果状态
            if (result.contains("\"status\": \"success\"")) {
                XxlJobHelper.log("即将上映电影数据同步成功");
                logger.info("即将上映电影数据同步成功");
            } else if (result.contains("\"status\": \"no_update_needed\"")) {
                XxlJobHelper.log("数据无需更新，跳过同步");
                logger.info("数据无需更新，跳过同步");
            } else if (result.contains("\"status\": \"error\"")) {
                XxlJobHelper.log("Python脚本执行出错: " + result);
                logger.error("Python脚本执行出错: {}", result);
                throw new RuntimeException("Python脚本执行出错: " + result);
            } else {
                XxlJobHelper.log("未知的执行结果状态: " + result);
                logger.warn("未知的执行结果状态: {}", result);
            }
            
        } catch (Exception e) {
            String errorMsg = "执行即将上映电影数据同步任务失败: " + e.getMessage();
            XxlJobHelper.log(errorMsg);
            logger.error(errorMsg, e);
            throw e;
        }
    }
}
