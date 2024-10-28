package org.osttra.exceptions;

import lombok.Builder;

@Builder
public record ApiError(String message, int statusCode) {
}
