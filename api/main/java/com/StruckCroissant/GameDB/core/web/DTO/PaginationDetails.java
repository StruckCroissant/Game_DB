package com.StruckCroissant.GameDB.core.web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class PaginationDetails {
  private final int page;

  private final int size;

  private final int totalPages;

  private final long totalElements;

  public PaginationDetails(
      int page,
      int size,
      int totalPages,
      long totalElements
  ) {
    this.page = page;
    this.size = size;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
  }

  @JsonProperty("page")
  public int getPage() {
    return page;
  }

  @JsonProperty("pageSize")
  public int getPageSize() {
    return size;
  }

  @JsonProperty("totalPages")
  public int getTotalPages() {
    return totalPages;
  }

  @JsonProperty("totalElements")
  public long getTotalElements()
  {
    return totalElements;
  }
}
