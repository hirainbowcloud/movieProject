package com.example.movie.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.movie.dto.OrderSummaryView;
import com.example.movie.model.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Integer> {

	// 某位會員的訂單 
	Page<Orders> findByMemberId(Integer memberId, Pageable pageable);
	
	// 管理者：用會員帳號模糊查詢訂單
	//Page<Orders> findByMember_UsernameContaining(String username, Pageable pageable);

	// 訂單摘要：會員帳號、電影、影廳、場次時間、張數，依訂單編號排序
//	@Query(value = """
//			SELECT m.username     AS username,
//			       o.order_id     AS orderId,
//			       o.total_amount AS totalAmount,
//			       o.order_time   AS orderTime,
//			       sh.show_date   AS showDate,
//			       sh.start_time  AS startTime,
//			       mov.title      AS title,
//			       ha.hall_name   AS hallName,
//			       CAST((SELECT COUNT(*) FROM ticket t WHERE t.order_id = o.order_id) AS SIGNED) AS ticketCount
//			FROM orders o
//			JOIN member   m   ON m.member_id    = o.member_id
//			JOIN showtime sh  ON sh.showtime_id = o.showtime_id
//			JOIN movie    mov ON mov.movie_id   = sh.movie_id
//			JOIN hall     ha  ON ha.hall_id     = sh.hall_id
//			ORDER BY o.order_id ASC
//			""", nativeQuery = true)
	
	@Query(value = """
			SELECT m.username     AS username,
			       o.order_id     AS orderId,
			       o.total_amount AS totalAmount,
			       o.order_time   AS orderTime,
			       sh.show_date   AS showDate,
			       sh.start_time  AS startTime,
			       mov.title      AS title,
			       ha.hall_name   AS hallName,
			       CAST((SELECT COUNT(*) FROM ticket t WHERE t.order_id = o.order_id) AS SIGNED) AS ticketCount
			FROM orders o
			JOIN member   m   ON m.member_id    = o.member_id
			JOIN showtime sh  ON sh.showtime_id = o.showtime_id
			JOIN movie    mov ON mov.movie_id   = sh.movie_id
			JOIN hall     ha  ON ha.hall_id     = sh.hall_id
			WHERE (:orderId IS NULL OR o.order_id = :orderId)
			  AND m.username LIKE CONCAT('%', :username, '%')
			ORDER BY o.order_id DESC
			""",
		countQuery = """
			SELECT COUNT(*)
			FROM orders o
			JOIN member m ON m.member_id = o.member_id
			WHERE (:orderId IS NULL OR o.order_id = :orderId)
			  AND m.username LIKE CONCAT('%', :username, '%')
			""",
		nativeQuery = true)
	Page<OrderSummaryView> findOrderSummaries
	(
			@Param("orderId") Integer orderId,
			@Param("username") String username,
			Pageable pageable
	);

}
