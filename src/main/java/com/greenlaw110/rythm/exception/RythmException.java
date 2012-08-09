package com.greenlaw110.rythm.exception;

import com.greenlaw110.rythm.internal.compiler.TemplateClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 14/02/12
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class RythmException extends FastRuntimeException {

    public int javaLineNumber = 0;
    public int templateLineNumber = -1;
    public String errorMessage = "";
    public String originalMessage = "";
    private TemplateClass templateClass = null;
    public String javaSource;
    public String templateSource;
    public String templateName;

    public RythmException(Throwable t, String templateName, String javaSource, String templateSource, int javaLineNumber, int templateLineNumber, String message) {
        super(message, t);
        this.templateName = templateName;
        this.javaSource = javaSource;
        this.templateSource = templateSource;
        this.javaLineNumber = javaLineNumber;
        this.templateLineNumber = templateLineNumber;
        this.originalMessage = message;
        this.errorMessage = message;
        resolveTemplateLineNumber();
    }

    public RythmException(String templateName, String javaSource, String templateSource, int javaLineNumber, int templateLineNumber, String message) {
        this(null, templateName, javaSource, templateSource, javaLineNumber, templateLineNumber, message);
    }

    public RythmException(String templateName, String javaSource, String templateSource, int javaLineNumber, String message) {
        this(null, templateName, javaSource, templateSource, javaLineNumber, -1, message);
    }

    public RythmException(Throwable t, TemplateClass tc, int javaLineNumber, int templateLineNumber, String message) {
        super(message, t);
        this.javaLineNumber = javaLineNumber;
        this.templateClass = tc;
        this.templateLineNumber = templateLineNumber;
        this.originalMessage = message;
        this.errorMessage = message;
        resolveTemplateLineNumber();
    }

    public RythmException(TemplateClass tc, int javaLineNumber, int templateLineNumber, String message) {
        this(null, tc, javaLineNumber, templateLineNumber, message);
    }

    public RythmException(TemplateClass tc, int javaLineNumber, String message) {
        this(null, tc, javaLineNumber, -1, message);
    }

    private static final Pattern P = Pattern.compile(".*\\/\\/line:\\s*([0-9]+).*");
    private void resolveTemplateLineNumber() {
        if (javaLineNumber != -1 && templateLineNumber == -1) {
            String[] lines = getJavaSource().split("\\n");
            if (javaLineNumber < lines.length) {
                String errorLine = lines[javaLineNumber - 1];
                Matcher m = P.matcher(errorLine);
                if (m.matches()) {
                    templateLineNumber = Integer.parseInt(m.group(1));
                }
            }
        }
    }

    public String getJavaSource() {
        if (null != javaSource) return javaSource;
        return (null == templateClass.javaSource) ? "" : templateClass.javaSource;
    }

    public String getTemplateSource() {
        if (null != templateSource) return templateSource;
        return templateClass.templateResource.asTemplateContent();
    }

    public String getTemplateName() {
        if (null != templateName) return templateName;
        return templateClass.getKey().toString();
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
