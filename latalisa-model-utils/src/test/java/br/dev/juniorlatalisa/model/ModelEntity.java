package br.dev.juniorlatalisa.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name = "entity_test")
public class ModelEntity implements Entidade, Codificavel<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	// TODO @CPF(required = true)
	private String cpf;
	// TODO @Pattern(regexp = Constants.EMAIL_PATTERN)
	private String email;

	@Override
	public Long getCodigo() {
		return codigo;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cpf, email, codigo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelEntity other = (ModelEntity) obj;
		return Objects.equals(cpf, other.cpf) && Objects.equals(email, other.email)
				&& Objects.equals(codigo, other.codigo);
	}

	@Override
	public String toString() {
		return "PlusEntityTest [" + (codigo != null ? "id=" + codigo + ", " : "")
				+ (cpf != null ? "cpf=" + cpf + ", " : "") + (email != null ? "email=" + email : "") + "]";
	}
}