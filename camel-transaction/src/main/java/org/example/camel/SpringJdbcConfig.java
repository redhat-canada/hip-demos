package org.example.camel;

import com.mysql.cj.jdbc.MysqlXADataSource;
import me.snowdrop.boot.narayana.core.jdbc.NarayanaDataSource;
import org.postgresql.xa.PGXADataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ComponentScan("org.example.camel")
public class SpringJdbcConfig {

    @Bean
    public DataSource postgres() {
        PGXADataSource pgxaDataSource = new PGXADataSource();
        pgxaDataSource.setUrl("jdbc:postgresql://localhost:5432/demo");
        pgxaDataSource.setUser("postgres");
        pgxaDataSource.setPassword("postgres");
        NarayanaDataSource dataSource = new NarayanaDataSource(pgxaDataSource);
        return dataSource;
    }

    @Bean
    public DataSource mysql() {
        MysqlXADataSource pgxaDataSource = new MysqlXADataSource();
        pgxaDataSource.setUrl("jdbc:mysql://localhost:3306/demo");
        pgxaDataSource.setUser("demo");
        pgxaDataSource.setPassword("demo");
        NarayanaDataSource dataSource = new NarayanaDataSource(pgxaDataSource);
        return dataSource;
    }
}
