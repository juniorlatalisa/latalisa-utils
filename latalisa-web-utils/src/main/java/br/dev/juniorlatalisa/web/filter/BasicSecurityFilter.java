package br.dev.juniorlatalisa.web.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionCookieConfig;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * <h1>Strict-Transport-Security</h1>
 * <p>
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
 * </p>
 * 
 * @author juniorlatalisa
 * @see <a href=
 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/Strict-Transport-Security">Strict-Transport-Security</a>
 * @see <a href=
 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/X-Frame-Options">X-Frame-Options</a>
 * @see <a href=
 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/X-XSS-Protection">X-XSS-Protection</a>
 * @see <a href=
 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/X-Content-Type-Options">X-Content-Type-Options</a>
 * @see <a href=
 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/Referrer-Policy">Referrer-Policy</a>
 * @see <a href=
 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/Content-Security-Policy">Content-Security-Policy</a>
 * @see <a href= "https://observatory.mozilla.org/">observatory.mozilla.org</a>
 */
@WebFilter(urlPatterns = "/*", displayName = "Segurança Básica", description = "Filtro para segurança básica da aplicação")
public class BasicSecurityFilter implements Filter {

	private boolean disableFilter;
	// Strict-Transport-Security
	private String directivesHSTS;
	// X-Frame-Options
	private XFrameOptions xFrameOption;
	// X-XSS-Protection
	private String directivesXXSSProtection;
	// X-Content-Type-Options
	private String directivesXContentTypeOptions;
	// Referrer-Policy
	private String directivesReferrerPolicy;
	// Content-Security-Policy
//	private String directivesCSP;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!disableFilter && request.isSecure() && response instanceof HttpServletResponse) {
			addHeader((HttpServletResponse) response);
		}
		chain.doFilter(request, response);
	}

	protected void addHeader(HttpServletResponse response) {
//		addHeader(response, "Content-Security-Policy", this.directivesCSP);
		addHeader(response, "Referrer-Policy", this.directivesReferrerPolicy);
		addHeader(response, "Strict-Transport-Security", this.directivesHSTS);
		addHeader(response, "X-Frame-Options", this.xFrameOption.name());
		addHeader(response, "X-XSS-Protection", this.directivesXXSSProtection);
		addHeader(response, "X-Content-Type-Options", this.directivesXContentTypeOptions);
	}

	protected void addHeader(HttpServletResponse response, String name, String value) {
		if (!response.containsHeader(name)) {
			response.addHeader(name, value);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (this.disableFilter = Boolean.parseBoolean(getInitParameter(filterConfig, "disableFilter", "false"))) {
			return;
		}
		// Strict-Transport-Security
		int maxAgeSeconds = Integer.parseInt(getInitParameter(filterConfig, "maxAgeSeconds", "31536000"));
		boolean includeSubDomains = Boolean.parseBoolean(getInitParameter(filterConfig, "includeSubDomains", "true"));
		boolean preload = Boolean.parseBoolean(getInitParameter(filterConfig, "preload", "true"));
		if (maxAgeSeconds <= 0) {
			throw new ServletException("Invalid maxAgeSeconds value :: " + maxAgeSeconds);
		}
		this.directivesHSTS = String.format("max-age=%s", maxAgeSeconds);
		if (includeSubDomains) {
			this.directivesHSTS += (" ; includeSubDomains");
			if (preload) {
				this.directivesHSTS += (" ; preload");
			}
		}
		// X-Frame-Options
		this.xFrameOption = XFrameOptions.valueOf(getInitParameter(filterConfig, "xFrameOption", "DENY"));
		// X-XSS-Protection
		this.directivesXXSSProtection = getInitParameter(filterConfig, "directivesXXSSProtection", "1; mode=block");
		// X-Content-Type-Options
		this.directivesXContentTypeOptions = getInitParameter(filterConfig, "directivesXContentTypeOptions", "nosniff");
		// Referrer-Policy
		this.directivesReferrerPolicy = getInitParameter(filterConfig, "directivesReferrerPolicy", "no-referrer");
		// Content-Security-Policy
//		this.directivesCSP = getInitParameter(filterConfig, "directivesCSP", "default-src 'self'");
	}

	protected String getInitParameter(FilterConfig filterConfig, String name, String def) {
		String value = filterConfig.getInitParameter(name);
		return value == null || value.isEmpty() ? def : value;
	}

	protected static boolean init(ServletContext servletContext) {
		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
		if (sessionCookieConfig == null) {
			return false;
		}
		sessionCookieConfig.setSecure(true);
		sessionCookieConfig.setHttpOnly(true);
		sessionCookieConfig.setName("__Host-sess");
		return sessionCookieConfig.isSecure() && sessionCookieConfig.isHttpOnly();
	}

	public static boolean init(ServletContext servletContext, boolean disableFilter) {
		if (disableFilter) {
			FilterRegistration reg = servletContext.getFilterRegistration(BasicSecurityFilter.class.getName());
			return reg != null && reg.setInitParameter("disableFilter", "true");
		}
		return init(servletContext);
	}

	/**
	 * Strict-Transport-Security
	 * 
	 * @see <a href=
	 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/Strict-Transport-Security">Strict-Transport-Security</a>
	 */
	public static boolean init(ServletContext servletContext, int maxAgeSeconds, boolean includeSubDomains,
			boolean preload, boolean disableFilter) {
		FilterRegistration reg = servletContext.getFilterRegistration(BasicSecurityFilter.class.getName());
		if (reg == null) {
			return false;
		}
		return init(servletContext) && reg.setInitParameter("maxAgeSeconds", Integer.toString(maxAgeSeconds))
				&& reg.setInitParameter("includeSubDomains", Boolean.toString(includeSubDomains))
				&& reg.setInitParameter("preload", Boolean.toString(preload))
				&& reg.setInitParameter("disableFilter", Boolean.toString(disableFilter));
	}

	/**
	 * X-Frame-Options
	 * 
	 * @see <a href=
	 *      "https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Headers/X-Frame-Options">X-Frame-Options</a>
	 */
	public static boolean init(ServletContext servletContext, XFrameOptions option) {
		FilterRegistration reg = servletContext.getFilterRegistration(BasicSecurityFilter.class.getName());
		return init(servletContext) && reg != null && reg.setInitParameter("xFrameOption", option.name());
	}

	public static enum XFrameOptions {
		/**
		 * A página não pode ser mostrada em um enquadramento, independente do site que
		 * esteja tentando fazer isso.
		 */
		DENY,
		/**
		 * A página só pode ser exibida em um enquadramento se for da mesma origem da
		 * página em si. A especificação deixa a cargo do navegador para decidir se esta
		 * opção se aplica ao nível mais alto, ao parente, ou à cadeia inteira,
		 * entretanto é discutido se a opção não é muito útil a não ser que todos os
		 * ancestrias estejam na mesma origem (veja bug 725490). Veja também Browser
		 * compatibility para mais detalhes de suporte.
		 */
		SAMEORIGIN,
	}

	@Override
	public int hashCode() {
		return Objects.hash(directivesHSTS, directivesReferrerPolicy, directivesXContentTypeOptions,
				directivesXXSSProtection, disableFilter, xFrameOption);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicSecurityFilter other = (BasicSecurityFilter) obj;
		return Objects.equals(directivesHSTS, other.directivesHSTS)
				&& Objects.equals(directivesReferrerPolicy, other.directivesReferrerPolicy)
				&& Objects.equals(directivesXContentTypeOptions, other.directivesXContentTypeOptions)
				&& Objects.equals(directivesXXSSProtection, other.directivesXXSSProtection)
				&& disableFilter == other.disableFilter && xFrameOption == other.xFrameOption;
	}

	@Override
	public String toString() {
		return "BasicSecurityFilter [disableFilter=" + disableFilter + ", "
				+ (directivesHSTS != null ? "directivesHSTS=" + directivesHSTS + ", " : "")
				+ (xFrameOption != null ? "xFrameOption=" + xFrameOption + ", " : "")
				+ (directivesXXSSProtection != null ? "directivesXXSSProtection=" + directivesXXSSProtection + ", "
						: "")
				+ (directivesXContentTypeOptions != null
						? "directivesXContentTypeOptions=" + directivesXContentTypeOptions + ", "
						: "")
				+ (directivesReferrerPolicy != null ? "directivesReferrerPolicy=" + directivesReferrerPolicy : "")
				+ "]";
	}
}