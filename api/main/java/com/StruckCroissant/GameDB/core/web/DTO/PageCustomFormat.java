package com.StruckCroissant.GameDB.core.web.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class PageCustomFormat<T> implements Page<T> {
  protected Page<T> page;

  public PageCustomFormat(Page<T> page) {
    this.page = page;
  }

  @JsonProperty("content")
  @NotNull
  public List<T> getContent() {
    return this.page.getContent();
  }

  @JsonProperty("pagination")
  public PaginationDetails getPaginationDetails() {
    return new PaginationDetails(
        this.page.getNumber(),
        this.page.getNumberOfElements(),
        this.page.getTotalPages(),
        this.page.getTotalElements()
    );
  }

  @Override
  @JsonIgnore
  public int getNumber() {
    return 0;
  }

  @Override
  @JsonIgnore
  public int getSize() {
    return page.getSize();
  }

  @Override
  @JsonIgnore
  public int getNumberOfElements() {
    return page.getNumberOfElements();
  }

  @Override
  @JsonIgnore
  public boolean hasContent() {
    return page.hasContent();
  }

  @Override
  @JsonIgnore
  @NotNull
  public Sort getSort() {
    return page.getSort();
  }

  @Override
  @JsonIgnore
  public boolean isFirst() {
    return page.isFirst();
  }

  @Override
  @JsonIgnore
  public boolean isLast() {
    return page.isLast();
  }

  @Override
  @JsonIgnore
  public boolean hasNext() {
    return page.hasNext();
  }

  @Override
  @JsonIgnore
  public boolean hasPrevious() {
    return page.hasPrevious();
  }

  @Override
  @JsonIgnore
  @NotNull
  public Pageable nextPageable() {
    return page.nextPageable();
  }

  @Override
  @JsonIgnore
  @NotNull
  public Pageable previousPageable() {
    return page.previousPageable();
  }

  @Override
  @JsonIgnore
  public int getTotalPages() {
    return 0;
  }

  @Override
  @JsonIgnore
  public long getTotalElements() {
    return page.getTotalElements();
  }

  @Override
  @JsonIgnore
  @NotNull
  public <U> Page<U> map(@NotNull Function<? super T, ? extends U> converter) {
    return page.map(converter);
  }

  @NotNull
  @Override
  @JsonIgnore
  public Iterator<T> iterator() {
    return page.iterator();
  }

  @Override
  @JsonIgnore
  public boolean isEmpty() {
    return Page.super.isEmpty();
  }

  @Override
  @NotNull
  @JsonIgnore
  public Pageable getPageable() {
    return Page.super.getPageable();
  }
}
