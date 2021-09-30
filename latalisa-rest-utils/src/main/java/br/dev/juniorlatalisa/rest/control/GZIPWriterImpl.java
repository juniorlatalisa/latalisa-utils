package br.dev.juniorlatalisa.rest.control;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
@GZIPWriterNeeded
public class GZIPWriterImpl implements WriterInterceptor, ContainerRequestFilter {

	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, "gzip");
		GZIPOutputStream gzip = new GZIPOutputStream(context.getOutputStream());
		try {
			context.setOutputStream(gzip);
			context.proceed();
		} finally {
			gzip.close();
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String encoding = requestContext.getHeaderString(HttpHeaders.ACCEPT_ENCODING);
		if (encoding == null || !encoding.contains("gzip")) {
			requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
					.entity("O retorno precisa estar preparado para o conteudo comprimido em gzip.").build());
			return;
		}
	}
}