package com.webrest.common.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> {
	Metadata metadata;
	T data;

	public static <T> Response<List<T>> paged(Page<T> page) {
		Pageable pageable = page.getPageable();
		PageResponse pageResponse = PageResponse.builder().page(pageable.getPageNumber()).size(pageable.getPageSize())
				.totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).build();
		return Response.<List<T>>builder().data(page.getContent())
				.metadata(Metadata.builder().page(pageResponse).build()).build();
	}
}
