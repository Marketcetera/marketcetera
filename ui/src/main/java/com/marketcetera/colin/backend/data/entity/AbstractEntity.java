package com.marketcetera.colin.backend.data.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private int version;

	public Long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, version);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AbstractEntity that = (AbstractEntity) o;
		return version == that.version &&
				Objects.equals(id, that.id);
	}
}
