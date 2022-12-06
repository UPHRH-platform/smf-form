package com.tarento.formservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tarento.formservice.models.InstituteCourses;
import com.tarento.formservice.models.InstitueFormExcelDto;

@Repository
public interface InstituteCoursesDao extends CrudRepository<InstituteCourses, Long>{
	
	@Query(value = "select distinct ic.district_name, ic.center_code, u.username,ic.degree, ic.course from institute_courses ic "
			+ " inner join user u on (u.id = ic.profile_id) where u.org_id = :orgId" ,
			nativeQuery=true )
			
	List<Object[]> findInstituteForm(Long orgId);

}
