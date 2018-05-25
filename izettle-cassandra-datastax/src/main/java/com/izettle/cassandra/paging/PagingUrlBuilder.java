package com.izettle.cassandra.paging;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.HttpHeaders.LINK;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class PagingUrlBuilder {

    public static final String DEFAULT_PAGE_NAME = "page";

    protected final String pageQueryName;
    protected final UriInfo uriInfo;
    protected final HttpServletResponse response;
    protected final Map<String, String> queryParams = new HashMap<>();

    public PagingUrlBuilder(final UriInfo uriInfo, final HttpServletResponse response) {
        this.uriInfo = requireNonNull(uriInfo, "uriInfo must not be null");
        this.response = requireNonNull(response, "response must not be null");
        this.pageQueryName = DEFAULT_PAGE_NAME;
    }

    public static PagingUrlBuilder create(UriInfo uriInfo, HttpServletResponse response) {
        return new PagingUrlBuilder(uriInfo, response);
    }

    public PagingUrlBuilder queryParam(String name, Object value) {
        queryParams.put(name, value.toString());
        return this;
    }

    public void setLinkOnResponse() {
        response.setHeader(LINK, createNextUrl());
    }

    public String createNextUrl() {
        final UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        queryParams.forEach(uriBuilder::queryParam);
        return "<" + uriBuilder + ">; rel=\"next\"";
    }

    public void setPageOnResponse(final PagingResult result) {
        result.getPagingState().ifPresent(page -> {
            final PagingUrlBuilder builder = PagingUrlBuilder.create(uriInfo, response);
            builder.queryParam(builder.pageQueryName, page.toString()).setLinkOnResponse();
        });
    }

    public static void setPageOnResponse(
        final UriInfo uriInfo,
        final HttpServletResponse response,
        final PagingResult result
    ) {
        new PagingUrlBuilder(uriInfo, response).setPageOnResponse(result);
    }
}
