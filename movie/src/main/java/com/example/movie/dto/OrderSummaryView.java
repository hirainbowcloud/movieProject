package com.example.movie.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface OrderSummaryView {
	String getUsername();
	Integer getOrderId();
	Integer getTotalAmount();
	LocalDateTime getOrderTime();
	LocalDate getShowDate();
	LocalTime getStartTime();
	String getTitle();
	String getHallName();
	Long getTicketCount();
}
