package com.lecture.user.repository.jdbc;

import com.lecture.user.repository.UserRepository;
import com.lecture.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AFTER 코드: JDBC 구현체
 * 
 * Repository 인터페이스를 구현하여 JDBC를 사용하는 구현체
 * Service 코드는 전혀 변경하지 않아도 JPA → JDBC로 교체 가능
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryUsingJdbc implements UserRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public User findById(Long id) {
        String sql = "SELECT id, email, name, password FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, userRowMapper(), id);
    }
    
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT id, email, name, password FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper(), email);
        } catch (Exception e) {
            throw new RuntimeException("User not found");
        }
    }
    
    private RowMapper<User> userRowMapper() {
        return new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new User(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getString("password")
                );
            }
        };
    }
}
