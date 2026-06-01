package com.example.self_management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("sys_finance") // 对应刚刚建的账本表
public class Finance {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId; // 必须有！数据隔离的钥匙
    private String type; // 收入 or 支出
    private BigDecimal amount; // 金额（用 BigDecimal 防止小数精度丢失）
    private String category; // 分类
    private String remark; // 备注
    private String recordDate; // 记录日期

    // --- 绝对安全的手动 Getter 和 Setter ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }
}