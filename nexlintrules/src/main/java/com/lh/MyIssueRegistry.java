package com.lh;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import java.util.Arrays;
import java.util.List;

/**
 * @author LiuHao
 * @time 16/10/24 下午7:49
 */
public class MyIssueRegistry extends IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        System.out.println("!!!!!!!!!!!!! ljf MyIssueRegistry lint rules works");
        return Arrays.asList(LoggerUsageDetector.ISSUE);
    }
}