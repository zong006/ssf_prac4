package vttp.ssf_prac4.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import vttp.ssf_prac4.model.LoginUser;
import vttp.ssf_prac4.repo.LoginUserRepo;
import vttp.ssf_prac4.util.Util;

@Service
public class LoginUserService {

    @Autowired
    LoginUserRepo loginUserRepo;

    public ResponseEntity<String> authenticate(LoginUser loginUser){
        String url = Util.url;
        RestTemplate restTemplate = new RestTemplate();
        try {
            RequestEntity<LoginUser> requestEntity = RequestEntity.post(url).contentType(MediaType.APPLICATION_JSON).body(loginUser);
            
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            return responseEntity;
        } catch (RestClientException e) {
            // e.printStackTrace();
            return ResponseEntity.badRequest().body("Authentication Failed.");
        }
    }

    public List<Integer> captchaElements(){
        Random r = new Random();
        int x = r.nextInt(1, 51);
        int y = r.nextInt(1, 51);
        List<Integer> elements = new ArrayList<>();
        elements.add(x);
        elements.add(y);

        int index = r.nextInt(Util.operations.size());
        elements.add(index);
        
        return elements;
    }

    public String captchaString(List<Integer> elements){
    
        return Integer.toString(elements.get(0)) + Util.operations.get(elements.get(elements.size()-1)) + Integer.toString(elements.get(1)); 
    }

    public boolean captchaIsCorrect(List<Integer> elements, int givenAnswer){
    // change this to include other operations
        int index = elements.get(elements.size()-1);
        String operation = Util.operations.get(index).strip();

        int correctAnswer = Integer.MAX_VALUE;
        
        if(operation.equals("+")){
            correctAnswer = elements.get(0) + elements.get(1);
        }
        else if (operation.equals("-")){
            correctAnswer = elements.get(0) - elements.get(1);
        }

        return correctAnswer == givenAnswer;
    }

    public void storeLockedUser(String key){
        loginUserRepo.storeLockedUser(key);
    }

    public boolean userIsLocked(String key){
        return  loginUserRepo.hasKey(key);
    }


}
