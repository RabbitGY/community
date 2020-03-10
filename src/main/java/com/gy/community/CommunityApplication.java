package com.gy.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @SpringBootApplication由
 * @SpringBootConfiguration表明该类是配置文件
 * @EnableAutoConfiguration表示启动自动配置
 * @ComponentScan组件扫描，自动装配bean
 */
@SpringBootApplication
public class CommunityApplication {

	@PostConstruct
	public void init(){
		// 解决netty启动冲突问题，redis和es底层依赖netty
		//Netty4Utils.setAvailableProcessors
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		//spring应用程序启动了，底层自动创建spring容器，创建之后会自动扫描某些包下的某些bean，将这些bean装配到容器中
		//扫描哪些bean，点击@SpringBootApplication，包含@ComponentScan，组件扫描，自动扫描配置类所在的包及子包的类，
		//并且类中有@Component（哪里都能用）、@Controller（处理请求）@Service（业务组件）、@Repository（访问数据库）的，
		//才可以被容器扫描
		//后三个都由Component实现
		SpringApplication.run(CommunityApplication.class, args);
	}

}
