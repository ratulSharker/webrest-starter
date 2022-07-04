package com.webrest.common.dto.datatable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import lombok.Getter;

@Getter
public class DataTableRequestModel {
	private final int draw;
	private String searchValue;
	private final Pageable pageable;

	public DataTableRequestModel(HttpServletRequest request) {
		this.draw = Integer.parseInt(request.getParameter("draw"));
		int start = Integer.parseInt(request.getParameter("start"));
		int length = Integer.parseInt(request.getParameter("length"));

		this.searchValue = request.getParameter("search[value]");

		List<Order> orders = new ArrayList<Order>();
		for (Iterator<String> iterator = request.getParameterNames().asIterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (key.startsWith("order") && key.endsWith("[dir]")) {

				String orderDirectionKey = key; // "order[0][dir]"
				String columnIndex = key.replaceAll("[^\\d.]", ""); // https://stackoverflow.com/a/10372905/2143128
				String orderColumnIndexKey = "order[" + columnIndex + "][column]"; // "order[0][column]"

				if (StringUtils.isNotBlank(request.getParameter(orderDirectionKey))
						&& StringUtils.isNotBlank(request.getParameter(orderColumnIndexKey))) {

					String columnKey = "columns[" + request.getParameter(orderColumnIndexKey) + "][data]";
					String direction = request.getParameter(orderDirectionKey);
					if (StringUtils.isNotEmpty(request.getParameter(columnKey))) {
						String propertyName = request.getParameter(columnKey);

						if ("asc".equals(direction)) {
							orders.add(new Order(Direction.ASC, propertyName));
						} else if ("desc".equals(direction)) {
							orders.add(new Order(Direction.DESC, propertyName));
						}
					}
				}
			}
		}

		this.pageable = PageRequest.of(start / length, length, Sort.by(orders));
	}
}
