package com.lecture.user.repository.jdbc;

import com.lecture.user.repository.UserRepository;
import com.lecture.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        String sql = "SELECT id, email, name, password, phone_number FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, userRowMapper(), id);
    }
    
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT id, email, name, password, phone_number FROM users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper(), email);
        } catch (Exception e) {
            throw new RuntimeException("User not found");
        }
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            // 새 유저 생성
            String sql = "INSERT INTO users (email, name, password, phone_number) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getPhoneNumber());
                return ps;
            }, keyHolder);
            
            Long generatedId = keyHolder.getKey().longValue();
            return new User(generatedId, user.getEmail(), user.getName(), user.getPassword(), user.getPhoneNumber());
        } else {
            // 기존 유저 업데이트
            String sql = "UPDATE users SET email = ?, name = ?, password = ?, phone_number = ? WHERE id = ?";
            jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getId()
            );
            return user;
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
                    rs.getString("password"),
                    rs.getString("phone_number")
                );
            }
        };
    }
}
