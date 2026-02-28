package com.contactapp.dao;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.contactapp.model.Person;

/**
 * Unit tests for PersonDAO.
 *
 * TASK 1
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonDAOTest {

    private final PersonDAO dao = new PersonDAO();

    @Test
    void testAddAndGetAll() {
        Person person = new Person("Doe", "John",
                "JD", "123456789",
                "Street", "john@test.com",
                LocalDate.of(2000, 1, 1));

        dao.add(person);

        List<Person> persons = dao.getAll();
        assertFalse(persons.isEmpty());
    }

    @Test
    void testUpdate() {
        List<Person> persons = dao.getAll();
        if (!persons.isEmpty()) {
            Person p = persons.get(0);
            p.setLastname("UpdatedName");
            dao.update(p);

            List<Person> updated = dao.getAll();
            assertEquals("UpdatedName", updated.get(0).getLastname());
        }
    }

    @Test
    void testDelete() {
        List<Person> persons = dao.getAll();
        if (!persons.isEmpty()) {
            int id = persons.get(0).getIdperson();
            dao.delete(id);

            List<Person> afterDelete = dao.getAll();
            assertTrue(afterDelete.stream().noneMatch(p -> p.getIdperson() == id));
        }
    }
}

