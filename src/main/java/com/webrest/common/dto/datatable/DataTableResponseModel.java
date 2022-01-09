package com.webrest.common.dto.datatable;

import java.util.List;
import org.springframework.data.domain.Page;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataTableResponseModel<T> {

	private int draw;

	private Long recordsTotal;

	private Long recordsFiltered;

	private List<T> data;

	private String message;

	public DataTableResponseModel(Page<T> page, int draw) {
		this.draw = draw;
		this.recordsTotal = page.getTotalElements();
		this.recordsFiltered = page.getTotalElements();
		data = page.getContent();
	}

	public DataTableResponseModel(String message) {
		this.message = message;
	}
}
