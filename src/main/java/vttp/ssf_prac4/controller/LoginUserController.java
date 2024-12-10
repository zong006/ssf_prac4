package vttp.ssf_prac4.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.ssf_prac4.model.LoginUser;
import vttp.ssf_prac4.service.LoginUserService;

@Controller
public class LoginUserController {
    
    @Autowired
    LoginUserService loginUserService;

    @GetMapping("/")
    public String loginPage(Model model, HttpSession httpSession){
        model.addAttribute("loginUser", new LoginUser());
        httpSession.setAttribute("hasCaptcha", false);
        httpSession.setAttribute("failedLoginCount", 0);
        return "view0";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/login")
    public String verifyLogin(@Valid @ModelAttribute LoginUser loginUser, BindingResult bindingResult, 
                                        HttpSession httpSession,
                                        @RequestParam(value = "captchaAnswer", required = false) String captchaAnswer){

        if (bindingResult.hasErrors()){
            return "view0";
        }
        httpSession.setAttribute("username", loginUser.getUsername());
        if (loginUserService.userIsLocked(loginUser.getUsername())){
            return "view2";
        }

        if ((boolean) httpSession.getAttribute("hasCaptcha")){
            // check captcha answer
            if (!loginUserService.captchaIsCorrect((List<Integer>) httpSession.getAttribute("elements"), Integer.parseInt(captchaAnswer))){
                
                regenerateNewCaptcha(httpSession);
                incrementFailedLoginAttempt(httpSession);
                
                // System.out.println("has captcha, incorrect answer : "+httpSession.getAttribute("failedLoginCount")); //

                if (hitThreeFailedLogin(httpSession)){
                    loginUserService.storeLockedUser((String) httpSession.getAttribute("username"));
                    return "view2";
                }
                return "view0";
            }
            // captcha correct
        }

        if (!userAuthenticated(loginUser)){ //user is not authenticated

            regenerateNewCaptcha(httpSession);
            incrementFailedLoginAttempt(httpSession);

            // System.out.println("no captcha, or captcha correct: " + httpSession.getAttribute("failedLoginCount")); //

            if (hitThreeFailedLogin(httpSession)){
                loginUserService.storeLockedUser((String) httpSession.getAttribute("username"));
                return "view2";
            }
            return "view0";
        }

        // pass authentication, including potential captcha check
        
        httpSession.setAttribute("authenticated", true);

        return "redirect:/protected";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession){
        httpSession.invalidate();

        return "redirect:/";
    }

    private boolean userAuthenticated(LoginUser loginUser){
        ResponseEntity<String> responseEntity = loginUserService.authenticate(loginUser);
            if (responseEntity.getStatusCode().value()==201){

                System.out.println("ACCEPTED");
                return true;
            }
            else if (responseEntity.getStatusCode().value()==400 || responseEntity.getStatusCode().value()==401){ //fail login
                System.out.println("BAD REQUEST");
                return false;
            }
            return false;
    }

    private void regenerateNewCaptcha(HttpSession httpSession){
        List<Integer> elements = loginUserService.captchaElements();
        String captchaString = loginUserService.captchaString(elements);
        httpSession.setAttribute("hasCaptcha", true);
        httpSession.setAttribute("captchaString", captchaString);
        // httpSession.setAttribute("elements", elements);
    }

    private void incrementFailedLoginAttempt(HttpSession httpSession){
        int failedLoginCount = (int) httpSession.getAttribute("failedLoginCount");
        httpSession.setAttribute("failedLoginCount", failedLoginCount + 1);
    }

    private boolean hitThreeFailedLogin(HttpSession httpSession){
        return (int) httpSession.getAttribute("failedLoginCount")>=3;
    }
}
