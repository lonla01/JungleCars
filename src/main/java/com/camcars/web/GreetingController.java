package com.camcars.web;

        import java.util.concurrent.atomic.AtomicLong;

        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

@Controller
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(    value = "/greeting",
                        method = RequestMethod.GET,
                        produces = {"application/xml", "application/json"} )
    @ResponseBody
    public Greeting greeting( @RequestParam(value="name", defaultValue="World") String name ) {
        return
                new Greeting( counter.incrementAndGet(),
                              String.format( template, name ) );
    }

}
