//package com.github.farzan6118.petclinic.common.repository;
//
//import ir.iau.daneshjouyar.accommodation.domain.entity.AuditableEntity;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import java.io.Serializable;
//import java.util.List;
//import java.util.Optional;
//
//public interface BaseEntityRepository<T extends AuditableEntity<ID>, ID extends Serializable>{
//
//    T save(T entity);
//    Boolean existsById(ID id);
//    Boolean existsByUuid(ID uuid);
//    Optional<T> findById(ID uuid);
//    List<T> findAll();
//    Page<T> findAll(Pageable pageable);
//    List<T> saveAll(List<T> entities);
//}
