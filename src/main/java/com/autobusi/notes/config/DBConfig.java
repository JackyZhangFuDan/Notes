package com.autobusi.notes.config;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.autobusi.notes.db.model.NoteRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses= {
		NoteRepository.class
})
@EntityScan(basePackages="com.autobusi.notes.db.model")
public class DBConfig {
	@Bean(name="dataSource")
	public DataSource getDataSource(){
		try {
			Context    context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/notes");
			ds.getConnection();
			if(ds == null){
				System.out.println("empty data source");
			}
			return ds;
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}