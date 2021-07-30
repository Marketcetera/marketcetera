package org.marketcetera.webui.data.service;

import org.marketcetera.webui.data.entity.SamplePerson;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, Integer> {

}