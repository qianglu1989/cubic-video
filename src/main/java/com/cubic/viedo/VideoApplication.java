package com.cubic.viedo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luqiang
 */
@SpringBootApplication
@MapperScan(basePackages ="com.cubic.viedo.mapper")
public class VideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoApplication.class, args);
	}

}
