package com.sensesai.sql.parser;

import com.google.common.collect.ImmutableList;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.util.Utils;
import lombok.Data;
import lombok.Getter;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOrderBy;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlCase;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * sql解析器.
 *
 * @author yinkaifeng
 * @since 2021-09-24 6:13 下午
 */
@Data
public class StellaSqlParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(StellaSqlParser.class);

    private SqlParser.Config config = SqlParser.config()
            .withLex(Lex.MYSQL);

    /**
     * 旧表名.
     */
    private String oldTableName;

    /**
     * 新表名.
     */
    private String newTableName;

    /**
     * 需要替换的节点列表.
     */
    private List<ReplaceNode> replaceNodeList = new ArrayList<>();

    /**
     * 构造方法.
     *
     * @param oldTableName 旧表名
     * @param newTableName 新表名
     */
    public StellaSqlParser(String oldTableName, String newTableName) {
        this("", oldTableName, newTableName);
    }

    /**
     * 构造方法.
     *
     * @param quotingStr   包围符号
     * @param oldTableName 旧表名
     * @param newTableName 新表名
     */
    public StellaSqlParser(String quotingStr, String oldTableName, String newTableName) {
        if (Utils.isEmpty(oldTableName)) {
            throw new SQLBuildException("旧表名不能为空");
        }
        if (Utils.isEmpty(newTableName)) {
            throw new SQLBuildException("新表名不能为空");
        }
        this.oldTableName = oldTableName;
        this.newTableName = newTableName;
        if (Quoting.DOUBLE_QUOTE.string.equals(quotingStr)) {
            config = SqlParser.config().withLex(Lex.ORACLE);
        } else if (Quoting.BRACKET.string.equals(quotingStr)) {
            config = SqlParser.config().withLex(Lex.SQL_SERVER);
        }
    }

    /**
     * 替换.
     *
     * @param sql sql语句
     * @return 替换后的sql
     */
    public String replace(String sql) {
        String targetSql = sql;
        parseSql(targetSql);
        //排序
        sort();
        String[] split = sql.split("\n");
        //总共有多少行
        int totalLine = split.length;
        int lastColumnNum = 0;
        //上一次处理的行号
        int lastLineNum = 0;
        StringBuilder sb = new StringBuilder();
        int size = replaceNodeList.size();
        //待替换节点位置信息
        SqlParserPos position;
        //待替换节点信息
        SqlIdentifier sqlIdentifier;
        //待替换节点在SqlIdentifier.names中的位置
        int pos;
        for (int i = 0; i < size; i++) {
            sqlIdentifier = replaceNodeList.get(i).getSqlIdentifier();
            pos = replaceNodeList.get(i).getIndex();
            position = sqlIdentifier.getComponentParserPosition(pos);
            final int columnNum = position.getColumnNum();
            final int lineNum = position.getLineNum();
            final int endColumnNum = position.getEndColumnNum();
            final int endLineNum = position.getEndLineNum();
            //前面不需要替换的行
            for (int j = lastLineNum; j < lineNum - 1; j++) {
                sb.append(split[j]).append("\n");
            }
            if (lineNum != lastLineNum) {
                lastColumnNum = 0;
            }
            //如果同一行中有多个需要替换的，这样处理起来
            String lineStr = split[lineNum - 1];
            int endIndex = columnNum - 1;
            //当前行前面的字符串
            String beforeStr = lineStr.substring(lastColumnNum, endIndex);
            sb.append(beforeStr);
            String value = lineStr.substring(columnNum - 1, endColumnNum);
            sb.append(value.replace(oldTableName, newTableName));
            //当前行后面的字符串，这里比较特殊，如果相同行后面还有需要处理的，则不能直接拼接后面的字符串
            //同一行是否有需要替换的数据
            boolean hasNext = false;
            if (i + 1 < size) {
                SqlParserPos nexPosition = replaceNodeList.get(i + 1).getSqlIdentifier()
                        .getComponentParserPosition(replaceNodeList.get(i + 1).getIndex());
                if (nexPosition.getLineNum() == lineNum) {
                    hasNext = true;
                }
            }
            int len = lineStr.length();
            boolean needAppendAfter = !hasNext && endColumnNum < len;
            if (needAppendAfter) {
                String afterStr = lineStr.substring(endColumnNum, len);
                sb.append(afterStr).append("\n");
            }
            lastColumnNum = endColumnNum;
            lastLineNum = endLineNum;
            //这里是别名换行了
            if (sqlIdentifier.names.size() == 2
                    && sqlIdentifier.getComponentParserPosition(1 - pos).getLineNum() != lineNum) {
                if (!needAppendAfter) {
                    sb.append("\n");
                }
            }
        }
        if (lastLineNum < totalLine) {
            for (int i = lastLineNum; i < totalLine; i++) {
                sb.append(split[i]).append("\n");
            }
        }
        return sb.toString();
    }

    private void sort() {
        replaceNodeList.stream().sorted((o1, o2) -> {
            SqlParserPos position1 = o1.getSqlIdentifier().getParserPosition();
            int lineNumber1 = position1.getLineNum();
            int columnNumber1 = position1.getColumnNum();

            SqlParserPos position2 = o2.getSqlIdentifier().getParserPosition();
            int lineNumber2 = position2.getLineNum();
            int columnNumber2 = position2.getColumnNum();

            if (lineNumber1 < lineNumber2 || (lineNumber1 == lineNumber2 && columnNumber1 < columnNumber2)) {
                return 1;
            } else {
                return -1;
            }
        });
    }

    /**
     * 解析sql.
     *
     * @param sql sql语句
     */
    private void parseSql(String sql) {
        SqlParser parser = SqlParser.create(sql, config);
        try {
            SqlNode sqlNode = parser.parseQuery();
            parseSqlNode(SqlSource.QUERY, sqlNode);
        } catch (Exception e) {
            LOGGER.error("sql解析失败", e);
            throw new SQLBuildException("sql解析失败", e);
        }
    }

    /**
     * 递归解析SqlNode.
     *
     * @param sqlSource 节点来源
     * @param sqlNode   SqlNode对象
     */
    private void parseSqlNode(SqlSource sqlSource, SqlNode sqlNode) {
        if (sqlNode instanceof SqlSelect) {
            parseSqlSelect((SqlSelect) sqlNode);
        } else if (sqlNode instanceof SqlOrderBy) {
            parseSqlOrderBy((SqlOrderBy) sqlNode);
        } else if (sqlNode instanceof SqlLiteral) {
            parseSqlLiteral((SqlLiteral) sqlNode);
        } else if (sqlNode instanceof SqlIdentifier) {
            parseSqlIdentifier(sqlSource, (SqlIdentifier) sqlNode);
        } else if (sqlNode instanceof SqlBasicCall) {
            parseSqlBasicCall(sqlSource, (SqlBasicCall) sqlNode);
        } else if (sqlNode instanceof SqlCase) {
            parseSqlCase(sqlSource, (SqlCase) sqlNode);
        } else if (sqlNode instanceof SqlJoin) {
            parseSqlJoin((SqlJoin) sqlNode);
        }
    }


    /**
     * 解析SqlOrderBy对象.
     *
     * @param sqlOrderBy SqlOrderBy对象
     */
    private void parseSqlOrderBy(SqlOrderBy sqlOrderBy) {
        //查询语句
        SqlNode query = sqlOrderBy.query;
        parseSqlNode(SqlSource.QUERY, query);
        //排序字段列表
        SqlNodeList orderList = sqlOrderBy.orderList;
        int size = orderList.size();
        for (int i = 0; i < size; i++) {
            parseSqlNode(SqlSource.ORDER_BY, orderList.get(i));
        }
    }

    /**
     * 解析SqlSelect对象.
     *
     * @param sqlSelect SqlSelect对象
     */
    private void parseSqlSelect(SqlSelect sqlSelect) {
        //select
        SqlNodeList selectList = sqlSelect.getSelectList();
        for (int i = 0, size = selectList.size(); i < size; i++) {
            parseSqlNode(SqlSource.SELECT, selectList.get(i));
        }
        //from
        SqlNode from = sqlSelect.getFrom();
        parseSqlNode(SqlSource.FROM, from);
        //where
        SqlNode where = sqlSelect.getWhere();
        if (where != null) {
            parseSqlNode(SqlSource.WHERE, where);
        }
        //group by
        SqlNodeList groupList = sqlSelect.getGroup();
        if (groupList != null && groupList.size() > 0) {
            for (int i = 0, size = groupList.size(); i < size; i++) {
                parseSqlNode(SqlSource.GROUP_BY, groupList.get(i));
            }
        }
        //having
        SqlNode having = sqlSelect.getHaving();
        if (having != null) {
            parseSqlNode(SqlSource.HAVING, having);
        }
        //order by
        SqlNodeList orderList = sqlSelect.getOrderList();
        if (orderList != null && orderList.size() > 0) {
            for (int i = 0, size = orderList.size(); i < size; i++) {
                parseSqlNode(SqlSource.ORDER_BY, orderList.get(i));
            }
        }
    }

    /**
     * 解析SqlJoin对象.
     *
     * @param sqlJoin SqlJoin对象
     */
    private void parseSqlJoin(SqlJoin sqlJoin) {
        SqlNode left = sqlJoin.getLeft();
        SqlNode right = sqlJoin.getRight();
        SqlNode condition = sqlJoin.getCondition();
        parseSqlNode(SqlSource.JOIN, left);
        parseSqlNode(SqlSource.JOIN, right);
        parseSqlNode(SqlSource.JOIN_CONDITION, condition);
    }

    /**
     * 解析SqlLiteral对象.
     *
     * @param sqlLiteral 对象
     */
    private void parseSqlLiteral(SqlLiteral sqlLiteral) {
        Object value = sqlLiteral.getValue();
    }

    /**
     * 解析SqlIdentifier对象.
     * SqlIdentifier这个对象可能是表名也可能是字段名，不好判断到底是表还是字段，故加上一个来源来判断比较方便。
     * 存在的情况：
     * 1、字段名，如：id
     * 2、表别名+字段名，如：t.id
     * 3、表名，如：test
     * 4、schema+表名，如：testsql.test
     *
     * @param sqlSource     节点来源
     * @param sqlIdentifier SqlIdentifier对象
     */
    private void parseSqlIdentifier(SqlSource sqlSource, SqlIdentifier sqlIdentifier) {
        ImmutableList<String> names = sqlIdentifier.names;
        //别名
        String alias = "";
        //名称
        String name = "";
        int size = names.size();
        if (size == 2) {
            alias = names.get(0);
            name = names.get(1);
        } else {
            name = names.get(0);
        }
        if (sqlSource == SqlSource.FROM || sqlSource == SqlSource.JOIN) {
            if (oldTableName.equals(name)) {
                if (size == 2) {
                    replaceNodeList.add(new ReplaceNode(1, sqlIdentifier));
                } else {
                    replaceNodeList.add(new ReplaceNode(0, sqlIdentifier));
                }
            }
        } else {
            if (oldTableName.equals(alias)) {
                replaceNodeList.add(new ReplaceNode(0, sqlIdentifier));
            }
        }
    }

    /**
     * 解析SqlBasicCall对象.
     *
     * @param sqlSource 节点来源
     * @param sqlCase   SqlCase对象
     */
    private void parseSqlCase(SqlSource sqlSource, SqlCase sqlCase) {
        SqlNodeList whenList = sqlCase.getWhenOperands();
        SqlNodeList thenList = sqlCase.getThenOperands();
        SqlNode elseNode = sqlCase.getElseOperand();
        int size = whenList.size();
        for (int i = 0; i < size; i++) {
            parseSqlNode(sqlSource, whenList.get(i));
            parseSqlNode(sqlSource, thenList.get(i));
        }
        parseSqlNode(sqlSource, elseNode);
    }

    /**
     * 解析SqlBasicCall对象.
     *
     * @param sqlSource    节点来源
     * @param sqlBasicCall SqlBasicCall对象
     */
    private void parseSqlBasicCall(SqlSource sqlSource, SqlBasicCall sqlBasicCall) {
        //操作
        SqlOperator operator = sqlBasicCall.getOperator();
        //操作数
        SqlNode[] operands = sqlBasicCall.getOperands();
        SqlKind kind = operator.getKind();
        switch (kind) {
            case AS:
                parseOperatorAs(sqlSource, operands);
                break;
            default:
                parseOperatorDefault(sqlSource, operands);
        }
    }

    /**
     * 解析AS操作.
     *
     * @param sqlSource 节点来源
     * @param operands  操作数
     */
    private void parseOperatorAs(SqlSource sqlSource, SqlNode[] operands) {
        int len = operands.length;
        for (int i = 0; i < len; i++) {
            parseSqlNode(sqlSource, operands[i]);
        }
    }

    /**
     * 默认解析操作.
     *
     * @param sqlSource 节点来源
     * @param operands  操作数
     */
    private void parseOperatorDefault(SqlSource sqlSource, SqlNode[] operands) {
        int len = operands.length;
        for (int i = 0; i < len; i++) {
            parseSqlNode(sqlSource, operands[i]);
        }
    }

    /**
     * sql类型枚举.
     */
    private enum SqlSource {
        QUERY,
        SELECT,
        FROM,
        JOIN,
        JOIN_CONDITION,
        WHERE,
        GROUP_BY,
        HAVING,
        ORDER_BY,
        OTHER;
    }

    /**
     * 待替换节点.
     */
    @Getter
    private static class ReplaceNode {
        /**
         * 待替换节点在names中的位置.
         */
        private int index;

        /**
         * 待替换节点.
         */
        private SqlIdentifier sqlIdentifier;

        ReplaceNode(int index, SqlIdentifier sqlIdentifier) {
            this.index = index;
            this.sqlIdentifier = sqlIdentifier;
        }
    }
}
