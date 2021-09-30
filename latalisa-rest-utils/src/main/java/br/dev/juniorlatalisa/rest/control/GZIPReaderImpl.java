package br.dev.juniorlatalisa.rest.control;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

@Provider
@GZIPReaderNeeded
public class GZIPReaderImpl implements ReaderInterceptor, ContainerRequestFilter {

	@Override
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
		context.setInputStream(new GZIPInputStream(context.getInputStream()));
		return context.proceed();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String encoding = requestContext.getHeaderString(HttpHeaders.CONTENT_ENCODING);
		if (encoding == null || !encoding.contains("gzip")) {
			requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
					.entity("Esse conteudo precisa estar comprimido em gzip.").build());
			return;
		}
	}
}