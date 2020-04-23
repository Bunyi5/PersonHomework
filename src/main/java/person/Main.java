package person;

import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Log4j2
public class Main {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");

    public static Person randomPerson() {
        Person person = new Person();
        Faker faker = new Faker();

        person.setName(faker.name().fullName());

        Date date = faker.date().birthday();
        LocalDate birthday = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        person.setDob(birthday);

        person.setGender(faker.options().option(Person.Gender.FEMALE, Person.Gender.MALE));

        Address address = new Address();
        address.setCountry(faker.address().country());
        address.setState(faker.address().state());
        address.setCity(faker.address().city());
        address.setStreetAddress(faker.address().streetAddress());
        address.setZip(faker.address().zipCode());
        person.setAddress(address);

        person.setEmail(faker.internet().emailAddress());
        person.setProfession(faker.company().profession());

        return person;
    }

    private static List<Person> getPersons() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Person l", Person.class).getResultList();
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            for (int i = 0; i < 1000; i++) {
                em.persist(randomPerson());
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        getPersons().forEach(log::info);
    }
}
