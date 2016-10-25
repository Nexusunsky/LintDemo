package com.lh;


import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.ConstructorInvocation;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Node;

public class MessageObtainDetector extends Detector implements Detector.JavaScanner {
    public static final Issue ISSUE = Issue.create(
            "MessageObtainNotUsed",
            "You should not call `new Message()` directly.",
            "You should not call `new Message()` directly. " +
                    "Instead, you should use `handler.obtainMessage` or `Message.Obtain()`.",
            Category.CORRECTNESS,
            9,
            Severity.ERROR,
            new Implementation(MessageObtainDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    /**
     * 事实上，这个方法的重写可以删除掉，任然可以达到检查new Message()的目的。
     * 因为只要在MessageObtainVisitor中重写了visitXXX()方法即可
     * 但是，getApplicableNodeTypes()重写本方法可以保证并提高效率
     */
    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(ConstructorInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(@NonNull JavaContext context) {
        return new MessageObtainVisitor(context);
    }

    private class MessageObtainVisitor extends ForwardingAstVisitor {
        private final JavaContext mContext;

        public MessageObtainVisitor(JavaContext context) {
            mContext = context;
        }

        @Override
        public boolean visitConstructorInvocation(ConstructorInvocation node) {
            /*
             这两行代码非常关键，它把lombok.ast中的类转换成了JavaParser中的类，
             如此一来我们就可以获取与此node对应的类、变量、方法或注解的详细信息。
             比如这里的node被转换为resolvedClass后，就可以获取与此类有关的类继承关系。
             */
            JavaParser.ResolvedNode resolveType = mContext.resolve(node.astTypeReference());
            JavaParser.ResolvedClass resolvedClass = (JavaParser.ResolvedClass) resolveType;
            /*
            还有一组比较有用的类型转换方法上面没有提到：
            ClassDeclaration surroundingClass = JavaContext.findSurroundingClass(node);
            Node surroundingMethod = JavaContext.findSurroundingMethod(node);
            用这组方法可以获取到此node外围包裹它的类或方法，这是JavaContext类提供的两个静态方法，
            可以将这两个方法与上面介绍的JavaParser中的ResolvedXXX类型配合使用。
            */
            if (resolvedClass != null && resolvedClass.isSubclassOf("android.os.Message", false)) {
                mContext.report(ISSUE, node, mContext.getLocation(node),
                        "You should not call `new Message()` directly.");
                return true;
            }
            return super.visitConstructorInvocation(node);
        }
    }

}
