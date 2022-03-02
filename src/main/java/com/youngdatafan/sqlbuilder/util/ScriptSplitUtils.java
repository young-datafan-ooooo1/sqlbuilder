package com.youngdatafan.sqlbuilder.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 脚本切割工具类.
 *
 * @author yinkaifeng
 * @since 2022-01-19 9:56 上午
 */
public class ScriptSplitUtils {

    /**
     * 脚本分割.
     *
     * @param scriptStr 脚本
     * @return 分割后的脚本
     */
    public static List<String> split(String scriptStr) {
        ScriptSplitter scriptSplit = new ScriptSplitter(scriptStr);
        return scriptSplit.split();
    }

    /**
     * 针对脚本用;字符切割，但是以下几种情况需要排除.
     * 1、注释：以--符号开头，且以换行符结尾，在这其中的忽略。
     * 2、注释：以/**\\/内的。
     * 3、''内的
     * 4、""内的
     */
    private static class ScriptSplitter {

        private static final char SEMICOLON = ';';

        private static final char DASH = '-';

        private static final char SLASH = '/';

        private static final char STAR = '*';

        private static final char SINGLE_QUOTE = '\'';

        private static final char DOUBLE_QUOTE = '"';

        private final String scriptStr;

        private boolean inSingleLineComment;

        private boolean inMultiLineComment;

        private boolean inSingleQuotes;

        private boolean inDoubleQuotes;

        private boolean ignoreComment = true;

        private char ch;

        private char next;

        ScriptSplitter(String scriptStr) {
            if (Objects.isNull(scriptStr) || "".equals(scriptStr.trim())) {
                throw new RuntimeException("脚本不能为空");
            }
            this.scriptStr = scriptStr;
        }

        public List<String> split() {
            List<String> result = new ArrayList<>();
            final int len = this.scriptStr.length();
            StringBuilder tmp = new StringBuilder();
            boolean isEndComment;
            for (int i = 0; i < len; i++) {
                ch = this.scriptStr.charAt(i);
                next = charAt(i + 1, len, this.scriptStr);
                isEndComment = false;
                //单行注释开始
                if (isStartSingleComment()) {
                    inSingleLineComment = true;
                }
                //单行注释结束
                if (isEndSingleComment()) {
                    inSingleLineComment = false;
                }

                //多行注释开始
                if (isStartMultiComment()) {
                    inMultiLineComment = true;
                }
                //多行注释结束
                if (isEndMultiComment()) {
                    inMultiLineComment = false;
                    isEndComment = true;
                    if (ignoreComment) {
                        i++;
                    }
                }

                //单引号
                if (isSingleQuote()) {
                    inSingleQuotes = !inSingleQuotes;
                }

                //双引号
                if (isDoubleQuote()) {
                    inDoubleQuotes = !inDoubleQuotes;
                }

                //脚本分割符
                if (isNewScript()) {
                    addSql(result, tmp);
                    tmp = new StringBuilder();
                } else {
                    if (ignoreComment) {
                        if (inSingleLineComment || inMultiLineComment) {
                            continue;
                        }
                        if (isEndComment) {
                            continue;
                        }
                    }
                    tmp.append(ch);
                }
            }
            addSql(result, tmp);
            return result;
        }

        /**
         * 单行注释开始.
         * 需要满足以下条件：
         * 1、以-- 开头
         * 2、不在多行注释、''、""中
         *
         * @return true or false
         */
        private boolean isStartSingleComment() {
            boolean flag = !(inMultiLineComment || inSingleQuotes || inDoubleQuotes);
            return !inSingleLineComment && flag && ch == DASH && next == DASH;
        }

        /**
         * 单行注释结束.
         * 需要满足以下条件：
         * 1、以换行符结束
         * 2、不在多行注释、''、""中
         *
         * @return true or false
         */
        private boolean isEndSingleComment() {
            boolean flag = !(inMultiLineComment || inSingleQuotes || inDoubleQuotes);
            return inSingleLineComment && flag && isNewLineChar(ch);
        }

        /**
         * 多行注释开始.
         * 需要满足以下条件：
         * 1、以/*开始
         * 2、不在单行注释、''、""中
         *
         * @return true or false
         */
        private boolean isStartMultiComment() {
            boolean flag = !(inSingleLineComment || inSingleQuotes || inDoubleQuotes);
            return flag && ch == SLASH && next == STAR;
        }

        /**
         * 多行注释结束.
         * 需要满足以下条件：
         * 1、以*\/结束
         * 2、不在单行注释、''、""中
         *
         * @return true or false
         */
        private boolean isEndMultiComment() {
            boolean flag = !(inSingleLineComment || inSingleQuotes || inDoubleQuotes);
            return inMultiLineComment && flag && ch == STAR && next == SLASH;
        }

        /**
         * 是否单引号.
         * 需要满足以下条件：
         * 1、以'开始
         * 2、不在单行注释、多行注释、""中
         *
         * @return true or false
         */
        private boolean isSingleQuote() {
            boolean flag = !(inSingleLineComment || inMultiLineComment || inDoubleQuotes);
            return flag && ch == SINGLE_QUOTE;
        }

        /**
         * 是双引号.
         * 需要满足以下条件：
         * 1、以"开始
         * 2、不在单行注释、多行注释、""中
         *
         * @return true or false
         */
        private boolean isDoubleQuote() {
            boolean flag = !(inSingleLineComment || inMultiLineComment || inSingleQuotes);
            return flag && ch == DOUBLE_QUOTE;
        }

        /**
         * 是否新脚本.
         * 需要满足以下条件：
         * 1、以;开始
         * 2、不在单行注释、多行注释、''、""中
         *
         * @return true or false
         */
        private boolean isNewScript() {
            boolean flag = !(inSingleLineComment
                    || inMultiLineComment
                    || inSingleQuotes
                    || inDoubleQuotes);
            return SEMICOLON == ch && flag;
        }

        private char charAt(int index, int len, String str) {
            if (index < len) {
                return str.charAt(index);
            }
            return ' ';
        }

        private boolean isNewLineChar(char ch) {
            if ('\n' == ch || '\r' == ch || '\t' == ch) {
                return true;
            }
            return false;
        }

        private void addSql(List<String> result, StringBuilder tmp) {
            String sql = tmp.toString();
            if (Objects.nonNull(sql) && !"".equals(sql = sql.trim())) {
                result.add(sql);
            }
        }
    }
}
