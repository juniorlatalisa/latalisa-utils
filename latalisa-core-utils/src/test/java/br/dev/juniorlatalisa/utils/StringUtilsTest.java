package br.dev.juniorlatalisa.utils;

import java.util.Date;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import br.dev.juniorlatalisa.Constants;

public class StringUtilsTest {

	@Test
	public void formatNumber() {
		Assert.assertTrue("1.543,90".equals(StringUtils.formatNumber(1543.9, "#,##0.00", Constants.BRAZIL)));
	}

	@Test
	public void hex() {
		final String value = "Teste HEX";
		String encode = StringUtils.encodeHEX(value.getBytes());
		byte[] decode = StringUtils.decodeHEX(encode);
		Assert.assertTrue(value.equals(new String(decode)));
	}

	@Test
	public void base64() {
		final String value = "Teste Base 64";
		String encode = StringUtils.encodeBase64(value.getBytes());
		byte[] decode = StringUtils.decodeBase64(encode);
		Assert.assertTrue(value.equals(new String(decode)));
	}

	@Test
	public void json() {
		TesteJSon value = new TesteJSon();
		value.setNome("Teste JSON");
		value.setIdade(31);
		value.setCadastro(new Date());
		String encode = StringUtils.encodeJSON(value);
		TesteJSon decode = StringUtils.decodeJSON(encode, TesteJSon.class);
		Assert.assertTrue(value.equals(decode));
	}

	public static class TesteJSon {
		private String nome;
		private Integer idade;
		private Date cadastro;

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public Integer getIdade() {
			return idade;
		}

		public void setIdade(Integer idade) {
			this.idade = idade;
		}

		public Date getCadastro() {
			return cadastro;
		}

		public void setCadastro(Date cadastro) {
			this.cadastro = cadastro;
		}

		@Override
		public int hashCode() {
			return Objects.hash(cadastro, idade, nome);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TesteJSon other = (TesteJSon) obj;
			return Objects.equals(cadastro, other.cadastro) && Objects.equals(idade, other.idade)
					&& Objects.equals(nome, other.nome);
		}

		@Override
		public String toString() {
			return "TesteJSon [" + (nome != null ? "nome=" + nome + ", " : "")
					+ (idade != null ? "idade=" + idade + ", " : "") + (cadastro != null ? "cadastro=" + cadastro : "")
					+ "]";
		}
	}
}