package com.webrest.common.dto.datatable;

import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.Getter;

@Getter
public class DataTableRequestModel {
	private final int draw;
	private final int start;
	private final int length;

	private String searchValue;

	public DataTableRequestModel(HttpServletRequest request) {
		this.draw = Integer.parseInt(request.getParameter("draw"));
		this.start = Integer.parseInt(request.getParameter("start"));
		this.length = Integer.parseInt(request.getParameter("length"));

		this.searchValue = request.getParameter("search[value]");
	}

	public Pageable getPageable() {
		return PageRequest.of(start / length, length);
	}
}
