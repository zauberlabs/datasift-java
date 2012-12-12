package org.datasift.qatest;

import org.datasift.User;
import org.datasift.EInvalidData;
import org.json.JSONException;
import org.json.JSONObject;

public class Dpu {
    public void h()
    {
    	User user;
        try {
            user = new User("","",true);
        }
        catch( EInvalidData e) {
        	
        }
        
        

        
    }
}