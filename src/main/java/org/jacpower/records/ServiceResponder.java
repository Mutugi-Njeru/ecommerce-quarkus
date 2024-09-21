package org.jacpower.records;

public record ServiceResponder(int statusCode, boolean isSuccess, Object message) {
}
