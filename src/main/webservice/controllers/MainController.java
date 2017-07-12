package controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/")
public class MainController {

    private static final ConcurrentMap<Integer, AtomicInteger> counters
            = new ConcurrentHashMap<Integer, AtomicInteger>(1800, 0.75f, 1000);


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getPageCode(HttpServletRequest request, HttpServletResponse response) {
        AtomicInteger counter = getCounter(request, response);
        if (counter.incrementAndGet() > 5) {
            return ResponseEntity.status(204).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

    private AtomicInteger getCounter(HttpServletRequest request, HttpServletResponse response) {
        AtomicInteger counter;
        Cookie cookie = getCookie(request);
        if (cookie != null) {
            counter = counters.get(Integer.parseInt(cookie.getValue()));
        } else {
            cookie = createNewCookie(response);
            counter = new AtomicInteger(0);
            counters.put(Integer.parseInt(cookie.getValue()), counter);
        }
        return counter;
    }


    private Cookie getCookie(HttpServletRequest request) {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cook : cookies) {
                if (cook.getName().equals("id")) {
                    cookie = cook;
                    break;
                }
            }
        }
        return cookie;
    }

    private Cookie createNewCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("id", getNextId());
        cookie.setMaxAge(300);
        response.addCookie(cookie);
        return cookie;
    }

    private String getNextId() {
        Random rand = new Random();
        return String.valueOf(rand.nextInt());
    }
}
