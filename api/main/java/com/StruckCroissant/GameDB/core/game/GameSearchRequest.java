package com.StruckCroissant.GameDB.core.game;

import com.StruckCroissant.GameDB.validation.NullOrNotBlank;
import javax.validation.constraints.DecimalMin;

@GameSearchRequestIsValid
public record GameSearchRequest(@NullOrNotBlank String name, @DecimalMin(value = "1") Integer id) {}
