package org.marketcetera.webui.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class DataGenerator {

//    @Bean
//    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, SamplePersonRepository samplePersonRepository,
//            UserRepository userRepository) {
//        return args -> {
//            Logger logger = LoggerFactory.getLogger(getClass());
//            if (samplePersonRepository.count() != 0L) {
//                logger.info("Using existing database");
//                return;
//            }
//            int seed = 123;
//
//            logger.info("Generating demo data");
//
//            logger.info("... generating 100 Sample Person entities...");
//            ExampleDataGenerator<SamplePerson> samplePersonRepositoryGenerator = new ExampleDataGenerator<>(
//                    SamplePerson.class, LocalDateTime.of(2021, 7, 30, 0, 0, 0));
//            samplePersonRepositoryGenerator.setData(SamplePerson::setId, DataType.ID);
//            samplePersonRepositoryGenerator.setData(SamplePerson::setFirstName, DataType.FIRST_NAME);
//            samplePersonRepositoryGenerator.setData(SamplePerson::setLastName, DataType.LAST_NAME);
//            samplePersonRepositoryGenerator.setData(SamplePerson::setEmail, DataType.EMAIL);
//            samplePersonRepositoryGenerator.setData(SamplePerson::setPhone, DataType.PHONE_NUMBER);
//            samplePersonRepositoryGenerator.setData(SamplePerson::setDateOfBirth, DataType.DATE_OF_BIRTH);
//            samplePersonRepositoryGenerator.setData(SamplePerson::setOccupation, DataType.OCCUPATION);
//            samplePersonRepositoryGenerator.setData(SamplePerson::setImportant, DataType.BOOLEAN_10_90);
//            samplePersonRepository.saveAll(samplePersonRepositoryGenerator.create(100, seed));
//
//            logger.info("... generating 2 User entities...");
//            User user = new User();
//            user.setName("John Normal");
//            user.setUsername("user");
//            user.setHashedPassword(passwordEncoder.encode("user"));
//            user.setProfilePictureUrl(
//                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
//            user.setRoles(Collections.singleton(Role.USER));
//            userRepository.save(user);
//            User admin = new User();
//            admin.setName("John Normal");
//            admin.setUsername("admin");
//            admin.setHashedPassword(passwordEncoder.encode("admin"));
//            admin.setProfilePictureUrl(
//                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
//            admin.setRoles(Collections.singleton(Role.ADMIN));
//            userRepository.save(admin);
//
//            logger.info("Generated demo data");
//        };
//    }

}