
---注释';'
select * from sys_user u;

/*
* 注释;
''
*/
show table status WHERE 1=1;

--常量带;
select ';' from dual;

select id,--主键
name, -- 名称
salary, /**薪水*/
'--常量字符串',
'/*常量注释*/'
from test t;

--/*测试创建脚本*/
CREATE TABLE SQL_REWRITER.CDI_EXCH_RATE (
    CCY_CD VARCHAR(100),
    DATA_DT DATE,
    EXCHG_CNY_AMT DECIMAL(20,2),
    EXCHG_USD_AMT DECIMAL(20,2)
);

-- 根据资产包编码获取资产包信息
select * from dm_asset_package_base_info t where t.ap_code='--';

-- 根据资产包编码获取资产包信息
select * from dm_asset_package_base_info t where t.ap_code='/*注释*/';