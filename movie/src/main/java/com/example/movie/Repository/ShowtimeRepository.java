package com.example.movie.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.movie.model.ShowTime;

@Repository
public interface ShowtimeRepository extends JpaRepository<ShowTime,Integer> {

	// 某部電影的場次（分頁）
	Page<ShowTime> findByMovieId(Integer movieId, Pageable pageable);

	// 新增時：同一影廳、同一日期時間是否已有場次
	@Query(value = """
			SELECT EXISTS
			(
				SELECT 1 
			    FROM movie_pos.showtime
			    WHERE hall_id = :hallId
			    AND show_date = :showDate
			    AND start_time = :startTime
			)
			""",
			nativeQuery = true)
			Long existsHallAndShowDateAndStartTime
			(
			@Param("hallId") Integer hallId,
			@Param("showDate")LocalDate showDate,
			@Param("startTime")LocalTime startTime
			);
	
	//boolean existsByHallIdAndShowDateAndStartTime(Integer hallId, LocalDate showDate, LocalTime startTime);

	// 修改時：同一影廳、同一日期時間是否已被「別的場次」使用
	@Query(value = """
			SELECT EXISTS 
			(
	            SELECT 1
	            FROM movie_pos.showtime
	            WHERE hall_id = :hallId
	              AND show_date = :showDate
	              AND start_time = :startTime
	              AND showtime_id <> :showtimeId
            )
			""",
			nativeQuery = true)
	Long existsHallAndShowDateAndStartTimeNotShowTimeId
	(Integer hallId, LocalDate showDate, LocalTime startTime, Integer showtimeId);
	
//	boolean existsByHallIdAndShowDateAndStartTimeAndShowtimeIdNot(
//			Integer hallId, LocalDate showDate, LocalTime startTime, Integer showtimeId);

	
	@Query(value = """			
			SELECT s.showtime_id, s.movie_id, s.hall_id, s.show_date, s.start_time
			FROM movie_pos.showtime s
			JOIN movie_pos.movie m
			ON s.movie_id = m.movie_id
			WHERE m.is_active = 1 and m.movie_id = :movieId
			ORDER BY s.hall_id
			""",
			countQuery = """
			SELECT COUNT(*)
			FROM movie_pos.showtime s
			JOIN movie_pos.movie m
			ON m.movie_id = s.movie_id
			WHERE m.is_active = 1 and m.movie_id = :movieId
			""",
			nativeQuery = true)
	List<ShowTime> findOnLineMovie(@Param("movieId") Integer movieId);
	//List<ShowTime> findByMovieIdAndShowDateGreaterThanEqualOrderByShowDateAscStartTimeAsc(
	//		Integer movieId, LocalDate showDate);
}
