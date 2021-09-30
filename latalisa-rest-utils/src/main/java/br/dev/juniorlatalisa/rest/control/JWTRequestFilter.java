package br.dev.juniorlatalisa.rest.control;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public abstract class JWTRequestFilter implements ContainerRequestFilter {

	/*
	 * https://datatracker.ietf.org/doc/html/rfc6750
	 */
	public static final String PORTADOR = "Bearer";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}
		if (!(authorizationHeader = authorizationHeader.trim()).startsWith(PORTADOR)) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
			return;
		}
		String token = authorizationHeader.substring(PORTADOR.length()).trim();
		if (token.isEmpty()) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
			return;
		}
		filter(requestContext, token);
	}

	protected abstract void filter(ContainerRequestContext requestContext, String token) throws IOException;
}
