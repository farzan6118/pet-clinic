//package com.github.farzan6118.petclinic.common.repository;
//
//import ir.iau.daneshjouyar.accommodation.repository.BaseEntityRepository;
//import ir.iau.daneshjouyar.accommodation.repository.jpa.BaseEntityJpa;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Optional;
//
//public class BaseEntityRepositoryImpl<U extends BaseEntityJpa<T, ID>,
//        ID extends Serializable> implements BaseEntityRepository<T, ID> {
//    protected final U jpaRepository;
//
//    public BaseEntityRepositoryImpl(U jpaRepository) {
//        this.jpaRepository = jpaRepository;
//    }
//
//    @Override
//    public T save(T entity) {
//        return jpaRepository.save(entity);
//    }
//
//    @Override
//    public Boolean existsById(ID id) {
//        return jpaRepository.existsById(id);
//    }
//
//    @Override
//    public Optional<T> findById(ID id) {
//        return jpaRepository.findById(id);
//    }
//
//    @Override
//    public List<T> findAll() {
//        return jpaRepository.findAll();
//    }
//
//    @Override
//    public List<T> saveAll(List<T> entities) {
//        return jpaRepository.saveAll(entities);
//    }
//
//    @Override
//    public Page<T> findAll(Pageable pageable) {
//        return jpaRepository.findAll(pageable);
//    }
//}
