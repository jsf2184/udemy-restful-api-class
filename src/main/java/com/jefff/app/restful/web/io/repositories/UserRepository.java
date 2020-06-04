package com.jefff.app.restful.web.io.repositories;

import com.jefff.app.restful.web.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

// We had been extending CrudRepository but when we added pagination, we switch to extending
// PagingAndSortingRepository
// public interface UserRepository extends CrudRepository<UserEntity, Long>


@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>
{
    // Unbelievable -- don't even have to write these methods. Spring JPA implements it for us
    // by virtue of naming it according to their standards.
    //
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);
}
