package mx.com.spring.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import mx.com.spring.app.auth.handler.LoginSuccesHandler;
import mx.com.spring.app.models.service.JpaUserDetailsService;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private JpaUserDetailsService userDetailsService;
	
	@Autowired
	private LoginSuccesHandler successHandler;
	
	//@Autowired 
	//private DataSource DataSource;

	@Autowired
	private  BCryptPasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()
		.antMatchers("/", "/css/**","/js/**", "/images/**", "/listar**","/locale", "/api/clientes/listar**", "/listar-rest**")
		.permitAll()
		/*.antMatchers("/ver/**").hasAnyRole("USER")
		.antMatchers("/uploads/**").hasAnyRole("USER")
		.antMatchers("/form/**").hasAnyRole("ADMIN")
		.antMatchers("/eliminar/**").hasAnyRole("ADMIN")
		.antMatchers("/factura/**").hasAnyRole("ADMIN") */
		.anyRequest().authenticated()
		.and().formLogin()
		.successHandler(successHandler)
		.loginPage("/login")
		.permitAll().and()
		.logout().permitAll()
		.and().exceptionHandling().accessDeniedPage("/error_403");
	}

	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {
	
		
		builder.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder);
		
		/*
		 * Implementacion con jdbc
		 * 
		 * builder.jdbcAuthentication().
		dataSource(DataSource).
		passwordEncoder(passwordEncoder)
		.usersByUsernameQuery("select username, password, enabled from users  where username=?")
		.authoritiesByUsernameQuery("select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id ) where u.username=?");
		//PasswordEncoder encoder = passwordEncoder;
		
		 UserBuilder users = User.builder().passwordEncoder(password -> {
			return encoder.encode(password);
		});
		// 
		 * 
		builder.inMemoryAuthentication()
		.withUser(users.username("admin")
				.password("12345").roles("ADMIN","USER"))
		.withUser(users.username("memo")
				.password("12345").roles("USER")); */
		}
		
	
}
