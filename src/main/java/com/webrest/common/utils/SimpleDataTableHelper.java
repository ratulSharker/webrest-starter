package com.webrest.common.utils;

import java.util.function.BiFunction;

import javax.servlet.http.HttpServletRequest;

import com.webrest.common.dto.datatable.DataTableRequestModel;
import com.webrest.common.dto.datatable.DataTableResponseModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class SimpleDataTableHelper<T> {


	private HttpServletRequest request;
	private BiFunction<Pageable, String, Page<T>> dataSupplier;

	public DataTableResponseModel<T> getResponse() {

		if (request == null) {
			log.info("`request` is null");
			return null;
		}

		if (dataSupplier == null) {
			log.info("`dataSupplier` is null");
			return null;
		}

		DataTableRequestModel dataTableRequest = new DataTableRequestModel(request);
		Pageable pageable = dataTableRequest.getPageable();
		String searchValue = dataTableRequest.getSearchValue();

		Page<T> page = dataSupplier.apply(pageable, searchValue);

		DataTableResponseModel<T> response = new DataTableResponseModel<T>(page, dataTableRequest.getDraw());
		return response;
	}
}
