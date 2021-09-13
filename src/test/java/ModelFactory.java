import com.sensesai.sql.enums.Cp;
import com.sensesai.sql.enums.Op;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.model.BinaryCondition;
import com.sensesai.sql.model.Condition;
import com.sensesai.sql.model.Conditions;
import com.sensesai.sql.model.InCondition;
import com.sensesai.sql.model.Model;

/**
 * 模型工厂类.
 *
 * @author yinkaifeng
 * @since 2021-09-07 9:35 上午
 */
public class ModelFactory {


    /**
     * 获取一个组合条件.
     *
     * @param cp     组合方式枚举
     * @param op     条件操作方式枚举
     * @param left   左值
     * @param rights 右值
     * @return 组合条件
     */
    public static Conditions getCpCondition(Cp cp, Op op, Model left, Model... rights) {
        Condition condition;
        switch (op) {
            case EQUAL:
                condition = BinaryCondition.equal(left, rights[0]);
                break;
            case NOT_EQUAL:
                condition = BinaryCondition.notEqual(left, rights[0]);
                break;
            case GREATER_THAN:
                condition = BinaryCondition.greaterThan(left, rights[0]);
                break;
            case GREATER_THAN_OR_EQUAL:
                condition = BinaryCondition.greaterThanOrEqual(left, rights[0]);
                break;
            case LESS_THAN:
                condition = BinaryCondition.lessThan(left, rights[0]);
                break;
            case LESS_THAN_OR_EQUAL:
                condition = BinaryCondition.lessThanOrEqual(left, rights[0]);
                break;
            case LIKE:
                condition = BinaryCondition.like(left, rights[0]);
                break;
            case NOT_LIKE:
                condition = BinaryCondition.notLike(left, rights[0]);
                break;
            case IS_NULL:
                condition = BinaryCondition.isNull(left);
                break;
            case NOT_NULL:
                condition = BinaryCondition.isNotNull(left);
                break;
            case IN:
                condition = InCondition.in(left, rights);
                break;
            case NOT_IN:
                condition = InCondition.notIn(left, rights);
                break;
            default:
                throw new SQLBuildException("暂不支持该条件操作方式");
        }
        return Conditions.getInstance(cp, condition);
    }
}
