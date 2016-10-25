package com.lh;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import java.util.Arrays;
import java.util.List;

/**
 * @author LiuHao
 * @time 16/10/25 下午3:45
 */
public class MessageConstractorIvocaIssueRegistery extends IssueRegistry {
    @Override
    public List<Issue> getIssues() {
        System.out.println("Nexus_Inspection of Message Invocation Issue!!!!!!!!");
        return Arrays.asList(MessageObtainDetector.ISSUE);
    }
}
