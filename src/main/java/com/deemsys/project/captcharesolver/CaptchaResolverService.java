package com.deemsys.project.captcharesolver;

import com.deathbycaptcha.Captcha;
import com.deathbycaptcha.Client;
import com.deathbycaptcha.HttpClient;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class CaptchaResolverService {

	public CaptchaResultForm resolveCaptcha(String username,String password,String base64Captcha){
		//convert Base64 to image bytes
		String base64Image = base64Captcha.split(",")[1];
		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
		CaptchaResultForm captchaResultForm=new CaptchaResultForm();
		Client client = (Client)(new HttpClient(username, password));
        //Client client = (Client)(new SocketClient(args[0], args[1]));
        client.isVerbose = false;
        try {
            try {
                System.out.println("Your balance is " + client.getBalance() + " US cents");
            } catch (IOException e) {
                System.out.println("Failed fetching balance: " + e.toString());
                //returnStr="Failed to Fetch Balance! Try Again Later!";
                captchaResultForm.setErrorCode("104");
                captchaResultForm.setErrorMessage("Failed fetching balance: " + e.toString());
                captchaResultForm.setStatus(0);
                return captchaResultForm;
            }

            Captcha captcha = null;
            try {
                // Upload a CAPTCHA and poll for its status with 120 seconds timeout.
                // Put you CAPTCHA image file name, file object, input stream, or
                // vector of bytes, and optional solving timeout (in seconds) here.
                captcha = client.decode(imageBytes, 120);
            } catch (IOException e) {
                captchaResultForm.setErrorCode("105");
                captchaResultForm.setErrorMessage("Failed uploading Image: " + e.toString());
                captchaResultForm.setStatus(0);
                return captchaResultForm;
               // returnStr= "Failed uploading CAPTCHA";
            }
            if (null != captcha) {
                System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);
                //returnStr="CAPTCHA " + captcha.id + " solved: " + captcha.text;
                if(captcha.text==null || captcha.text.equals("")){
                	captchaResultForm.setCaptchaId(Integer.toString(captcha.id));
                	captchaResultForm.setStatus(2);
                	return captchaResultForm;
                }
                else if(!captcha.text.equals("")){
                	captchaResultForm.setText(captcha.text.toUpperCase());
                	captchaResultForm.setStatus(1);
                	captchaResultForm.setCaptchaId(Integer.toString(captcha.id));
                	return captchaResultForm;
                }
                // Report incorrectly solved CAPTCHA if necessary.
                // Make sure you've checked if the CAPTCHA was in fact incorrectly
                // solved, or else you might get banned as abuser.
                /*try {
                    if (client.report(captcha)) {
                        System.out.println("Reported as incorrectly solved");
                    } else {
                        System.out.println("Failed reporting incorrectly solved CAPTCHA");
                    }
                } catch (IOException e) {
                    System.out.println("Failed reporting incorrectly solved CAPTCHA: " + e.toString());
                }*/
            } else {
            	captchaResultForm.setErrorMessage("Failed solving CAPTCHA");
            	captchaResultForm.setStatus(0);
            	captchaResultForm.setErrorCode("103");
            	return captchaResultForm;
            }
        } catch (Exception e) {
        	captchaResultForm.setErrorMessage("Error"+e.toString());
        	captchaResultForm.setStatus(0);
        	captchaResultForm.setErrorCode("102");
        	return captchaResultForm;
        }
		return captchaResultForm;
	}
	
	public CaptchaResultForm getTextbyCaptcha(String captchaResponse) throws JSONException{
        CaptchaResultForm captchaResultForm=new CaptchaResultForm();
        JSONObject jsonObj=new JSONObject(captchaResponse);
        System.out.println(jsonObj);
        captchaResultForm.setCaptchaId(jsonObj.get("captcha").toString());
        if(jsonObj.get("text").equals("")||jsonObj.get("text")==null){
            captchaResultForm.setStatus(1);
        }
        else if(!jsonObj.get("text").equals("")){
            captchaResultForm.setStatus(2);
        }
        captchaResultForm.setText(jsonObj.get("text").toString().toUpperCase());
        return captchaResultForm;

	}
	public Boolean reportCaptcha(CaptchaResolverForm captchaResolverForm) throws NumberFormatException, IOException, com.deathbycaptcha.Exception{
		
		Client client = (Client)(new HttpClient(captchaResolverForm.getUsername(), captchaResolverForm.getPassword()));
		client.isVerbose=false;
		Boolean status=client.report(Integer.parseInt(captchaResolverForm.getCaptchaId()));
		return status;
	
	}
	
}
