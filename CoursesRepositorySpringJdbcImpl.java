package ru.itis.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import ru.itis.models.Course;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursesRepositorySpringJdbcImpl implements CoursesRepository {

    private static final String COURSE_TABLE = "course";
    private static final String SQL_SELECT_ALL = "SELECT * FROM course";

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertCourse;

    public CoursesRepositorySpringJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertCourse = new SimpleJdbcInsert(dataSource)
                .withTableName(COURSE_TABLE)
                .usingGeneratedKeyColumns("id");
    }

    private static final RowMapper<Course> courseRowMapper = (rs, rowNum) -> {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setName(rs.getString("name"));
        course.setDescription(rs.getString("description"));
        return course;
    };

    @Override
    public void save(Course course) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", course.getName());
        params.put("description", course.getDescription());

        int generatedId = insertCourse.executeAndReturnKey(params).intValue();
        course.setId(generatedId);
    }

    @Override
    public List<Course> findAll() {
        return jdbcTemplate.query(SQL_SELECT_ALL, courseRowMapper);
    }
}
