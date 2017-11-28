package freakydevelopers.dhobikart.Dialog;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import freakydevelopers.dhobikart.Resources.Logger;

/**
 * Created by PURUSHOTAM on 11/30/2016.
 */

public class MyFireBaseInstanceId extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        String recent_token_id = FirebaseInstanceId.getInstance().getToken();
        Logger.d("Token Generated:" + recent_token_id);
    }
}
