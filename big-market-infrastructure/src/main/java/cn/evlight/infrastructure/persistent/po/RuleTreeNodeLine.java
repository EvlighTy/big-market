package cn.evlight.infrastructure.persistent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author evlight
 * @since 2024-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rule_tree_node_line")
public class RuleTreeNodeLine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则树ID
     */
    private String treeId;

    /**
     * 规则Key节点 From
     */
    private String ruleNodeFrom;

    /**
     * 规则Key节点 To
     */
    private String ruleNodeTo;

    /**
     * 限定类型；1:=;2:>;3:<;4:>=;5<=;6:enum[枚举范围];
     */
    private String ruleLimitType;

    /**
     * 限定值（到下个节点）
     */
    private String ruleLimitValue;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
