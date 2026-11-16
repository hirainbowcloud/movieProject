package com.example.movie.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;


@JsonPropertyOrder({ "orderId", "username", "title",
	"hallName", "ticketCount", "totalAmount", "showDate", "startTime",
	"orderTime"})

@Data
public class OrderSummaryResponse {
	
	private String username;
	private Integer orderId;
	private Integer totalAmount;
	private LocalDateTime orderTime;
	private LocalDate showDate;
	private LocalTime startTime;
	private String title;
	private String hallName;
	private Long ticketCount;
	
	public static OrderSummaryResponse from(OrderSummaryView v)
	{
		OrderSummaryResponse res = new OrderSummaryResponse();
		res.setUsername(v.getUsername());
		res.setOrderId(v.getOrderId());
		res.setTotalAmount(v.getTotalAmount());
		res.setOrderTime(v.getOrderTime());
		res.setShowDate(v.getShowDate());
		res.setStartTime(v.getStartTime());
		res.setTitle(v.getTitle());
		res.setHallName(v.getHallName());
		res.setTicketCount(v.getTicketCount());
		return res;
	}

}
