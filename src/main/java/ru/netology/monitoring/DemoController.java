package ru.netology.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class DemoController {
    private final Counter demoCalls;

    public DemoController(MeterRegistry registry) {
        this.demoCalls = Counter.builder("demo_controller_calls_total")
                .description("Number of calls to demo controller endpoints")
                .register(registry);
    }

    @GetMapping("/api/ok")
    public String ok() { demoCalls.increment(); return "OK"; }

    @GetMapping("/api/not-found")
    public String notFound() {
        demoCalls.increment();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demo 404 response");
    }

    @GetMapping("/api/error")
    public String error() {
        demoCalls.increment();
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Demo 500 response");
    }

    @GetMapping("/api/allocate/{megabytes}")
    public String allocate(@PathVariable int megabytes) {
        demoCalls.increment();
        if (megabytes < 1 || megabytes > 256) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "megabytes must be between 1 and 256");
        }
        MemoryLoad.allocate(megabytes);
        return "Allocated " + megabytes + " MiB; retained=" + MemoryLoad.retainedMegabytes() + " MiB";
    }
}
