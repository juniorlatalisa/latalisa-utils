package br.dev.juniorlatalisa.web.control;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Se um site aceitar uma conexão por meio de HTTP e redirecionar para HTTPS, os
 * visitantes poderão se comunicar inicialmente com a versão não criptografada
 * do site antes de serem redirecionados, se, por exemplo, o visitante digitar
 * http://www.foo.com/ ou até mesmo apenas foo.com Isso cria uma oportunidade
 * para um ataque man-in-the-middle. O redirecionamento pode ser explorado para
 * direcionar os visitantes a um site mal-intencionado em vez da versão segura
 * do site original. O cabeçalho HTTP Strict Transport Security informa ao
 * navegador que ele nunca deve carregar um site usando HTTP e deve converter
 * automaticamente todas as tentativas de acessar o site usando HTTP para
 * solicitações HTTPS.
 * 
 * @author juniorlatalisa
 */
@WebFilter(urlPatterns = "/*", //
		displayName = "Strict-Transport-Security", //
//		initParams = { //
//				@WebInitParam(name = "maxAgeSeconds", value = "31536000"), // Um ano
//				@WebInitParam(name = "includeSubDomains", value = "true"), //
//				@WebInitParam(name = "disableFilter", value = "false") //
//		}, //
		description = "O cabeçalho de resposta HTTP Strict-Transport-Security (geralmente abreviado como HSTS) permite que um site informe aos navegadores que ele deve ser acessado apenas por HTTPS, em vez de usar HTTP.")
public class HSTSFilter implements Filter {

//	http://www.mastertheboss.com/web/jboss-web-server/configuring-strict-transport-security-hsts-on-wildfly/
//	https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/Strict-Transport-Security

//	private static final String HEADER_NAME = "Strict-Transport-Security";
//	private static final String MAX_AGE_DIRECTIVE = "max-age=%s";
//	private static final String INCLUDE_SUB_DOMAINS_DIRECTIVE = "includeSubDomains";

	private int maxAgeSeconds = 0;
	private boolean includeSubDomains = false;
	private boolean disableFilter = false;
	private boolean preload = false;
	private String directives;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!disableFilter && request.isSecure() && response instanceof HttpServletResponse) {
			addHeader((HttpServletResponse) response);
		}
		chain.doFilter(request, response);
	}

	protected void addHeader(HttpServletResponse response) {
		response.addHeader("Strict-Transport-Security", this.directives);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.maxAgeSeconds = Integer.parseInt(getInitParameter(filterConfig, "maxAgeSeconds", "31536000"));
		this.includeSubDomains = Boolean.parseBoolean(getInitParameter(filterConfig, "includeSubDomains", "true"));
		this.preload = Boolean.parseBoolean(getInitParameter(filterConfig, "preload", "true"));
		this.disableFilter = Boolean.parseBoolean(getInitParameter(filterConfig, "disableFilter", "false"));
		if (this.maxAgeSeconds <= 0) {
			throw new ServletException("Invalid maxAgeSeconds value :: " + maxAgeSeconds);
		}
		this.directives = String.format("max-age=%s", this.maxAgeSeconds);
		if (this.includeSubDomains) {
			this.directives += (" ; includeSubDomains");
			if (this.preload) {
				this.directives += (" ; preload");
			}
		}
	}

	protected String getInitParameter(FilterConfig filterConfig, String name, String def) {
		String value = filterConfig.getInitParameter("maxAgeSeconds");
		return value == null || value.isEmpty() ? def : value;
	}

	public static boolean init(ServletContext servletContext, int maxAgeSeconds, boolean includeSubDomains,
			boolean preload, boolean disableFilter) {
		FilterRegistration reg = servletContext.getFilterRegistration(HSTSFilter.class.getName());
		if (reg == null) {
			return false;
		}
		return reg.setInitParameter("maxAgeSeconds", Integer.toString(maxAgeSeconds))
				&& reg.setInitParameter("includeSubDomains", Boolean.toString(includeSubDomains))
				&& reg.setInitParameter("preload", Boolean.toString(preload))
				&& reg.setInitParameter("disableFilter", Boolean.toString(disableFilter));
	}

	public static boolean init(ServletContext servletContext, boolean disableFilter) {
		if (disableFilter) {
			FilterRegistration reg = servletContext.getFilterRegistration(HSTSFilter.class.getName());
			return reg != null && reg.setInitParameter("disableFilter", "true");
		}
		return true;
	}
}