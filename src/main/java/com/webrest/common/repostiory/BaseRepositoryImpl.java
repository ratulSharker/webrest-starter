package com.webrest.common.repostiory;


import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class BaseRepositoryImpl<ENTITY, ID> extends SimpleJpaRepository<ENTITY, ID>
		implements BaseRepository<ENTITY, ID> {

	private final EntityManager entityManager;

	public BaseRepositoryImpl(JpaEntityInformation<ENTITY, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public void detach(ENTITY entity) {
		entityManager.detach(entity);
	}

}