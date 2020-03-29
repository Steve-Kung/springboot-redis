package cn.stevekung.config;

import org.hibernate.dialect.MySQL55Dialect;

public class MysqlConfig extends MySQL55Dialect {
    @Override
    public String getTableTypeString() {
        return "ENGINE=INNODB DEFAULT CHARSET=utf8";
    }
}
