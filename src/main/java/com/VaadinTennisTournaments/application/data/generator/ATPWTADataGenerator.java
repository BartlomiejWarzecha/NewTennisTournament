package com.VaadinTennisTournaments.application.data.generator;

import com.VaadinTennisTournaments.application.data.entity.ATP.ATP;
import com.VaadinTennisTournaments.application.data.repository.ATPRepository;
import com.VaadinTennisTournaments.application.data.repository.StageRepository;
import com.VaadinTennisTournaments.application.data.repository.WTARepository;
import com.VaadinTennisTournaments.application.data.entity.Stage;
import com.VaadinTennisTournaments.application.data.entity.WTA.WTA;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringComponent
public class ATPWTADataGenerator {

    @Bean
    public CommandLineRunner loadATPWTAData(ATPRepository atpRepository, StageRepository stageRepository, WTARepository wtaRepository) {

        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (atpRepository.count() != 0L || wtaRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            List<Stage> stages = stageRepository
                    .saveAll(Stream.of("1/256", "1/128", "1/64", "1/32", "1/16", "1/8", "QF", "SF", "F")
                            .map(Stage::new).collect(Collectors.toList()));

            ExampleDataGenerator<ATP> atpTournamentGenerator = new ExampleDataGenerator<>(ATP.class,
                    LocalDateTime.now());
            atpTournamentGenerator.setData(ATP::setNickname, DataType.BOOK_TITLE);
            atpTournamentGenerator.setData(ATP::setAtpTournament, DataType.BOOK_GENRE);
            atpTournamentGenerator.setData(ATP::setPlayer, DataType.LAST_NAME);

            ExampleDataGenerator<WTA> wtaTournamentGenerator = new ExampleDataGenerator<>(WTA.class,
                    LocalDateTime.now());
            wtaTournamentGenerator.setData(WTA::setNickname, DataType.BOOK_TITLE);
            wtaTournamentGenerator.setData(WTA::setWtaTournament, DataType.BOOK_GENRE);
            wtaTournamentGenerator.setData(WTA::setPlayer, DataType.LAST_NAME);

            logger.info("... generating 5 WTA/ATP entities...");

            Random r = new Random(seed);
            List<ATP> atps = atpTournamentGenerator.create(5, seed).stream().peek(atp -> {
                atp.setStage(stages.get(r.nextInt(stages.size())));
            }).collect(Collectors.toList());

            List<WTA> wtas = wtaTournamentGenerator.create(5, seed).stream().peek(wta -> {
                wta.setStage(stages.get(r.nextInt(stages.size())));
            }).collect(Collectors.toList());

            atpRepository.saveAll(atps);
            wtaRepository.saveAll(wtas);

        };
    }

}