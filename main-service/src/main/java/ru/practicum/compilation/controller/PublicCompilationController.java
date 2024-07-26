package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

     private final CompilationService compilationService;


    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable Long compId) {
        log.info("Start fetching compilation with id = {}", compId);
        CompilationDto fetchedCompilation = compilationService.findById(compId);
        log.info("Finish fetching compilation with id = {}", fetchedCompilation.getId());
        return fetchedCompilation;
    }

    @GetMapping
    public List<CompilationDto> search(@RequestParam(required = false, defaultValue = "false") String pinned,
                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
                                       @RequestParam(required = false, defaultValue = "10") @Positive Long size) {
        log.info("Start fetching pinned compilations with search parameters from = {} and size = {}", from, size);
        List<CompilationDto> fetchedCompilations = compilationService.search(Boolean.valueOf(pinned), from, size);
        log.info("Finish fetching pinned compilations with search parameters from = {} and size = {}", from, size);
        return fetchedCompilations;
    }

}