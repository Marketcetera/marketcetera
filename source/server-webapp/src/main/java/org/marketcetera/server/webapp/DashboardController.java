package org.marketcetera.server.webapp;

import java.util.Comparator;

import org.marketcetera.server.security.PasswordManager;
import org.marketcetera.server.service.UserManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class DashboardController
{
    @RequestMapping(value = "/")
    public String home()
    {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        SLF4JLoggerProxy.info(DashboardController.class,
                              "DashboardController: Passing through: {}",
                              currentUser);
//        User test = new User();
//        test.setName("colin");
//        test.setDescription("colin's user");
//        test.setActive(true);
//        test.setHashedPassword(passwordManager.encodePassword(test,
//                                                              "password"));
//        userManager.write(test);
//        List<User> users = userManager.getUsers();
//        System.out.println("Found: " + users);
        return "dashboard";
    }
    @RequestMapping(value = "/compare", method = RequestMethod.GET)
    public String compare(@RequestParam("input1") String input1,
                          @RequestParam("input2") String input2,
                          Model model)
    {
        int result = comparator.compare(input1,
                                        input2);
        String inEnglish = (result < 0) ? "less than" : (result > 0 ? "greater than" : "equal to");
        String output = "According to our Comparator, '" + input1 + "' is " + inEnglish + "'" + input2 + "'";
        model.addAttribute("output",
                           output);
        return "compareResult";
    }
    @Autowired
    Comparator<String> comparator;
    @Autowired
    private UserManager userManager;
    @Autowired
    private PasswordManager passwordManager;
}
