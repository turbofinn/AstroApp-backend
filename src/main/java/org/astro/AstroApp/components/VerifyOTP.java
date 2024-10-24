package org.astro.AstroApp.components;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.astro.AstroApp.dbmappers.DB_AuthenticationOTP;
//import org.astro.AstroApp.dbmappers.DB_Table;
import org.astro.AstroApp.dbmappers.DB_User;
import org.astro.AstroApp.util.Constants;

public class VerifyOTP implements RequestHandler<VerifyOTP.VerifyOtpInput,VerifyOTP.VerifyOtpOutput> {
    public static void main(String[] args) {
        VerifyOtpInput input = new VerifyOtpInput();
        input.setMobileNo("7985159933");
        input.setOtp("1234");
        System.out.println(new Gson().toJson(new VerifyOTP().handleRequest(input,null)));
    }

    @Override
    public VerifyOtpOutput handleRequest(VerifyOtpInput input, Context context) {
        if(input==null || input.getOtp()==null || input.getMobileNo()==null){
            return new VerifyOtpOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
        DB_AuthenticationOTP otp = DB_AuthenticationOTP.fetchOtpByMobileNo(input.mobileNo);

        System.out.println(new Gson().toJson(otp));
        if(otp == null ){
            return new VerifyOtpOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }

        if(otp.getOtp().equalsIgnoreCase(input.otp)){
            DB_User dbUser = DB_User.fetchUserByMobileNo(input.mobileNo);
            if(dbUser==null){
                DB_User user = new DB_User();
                user.setMobileNo(input.mobileNo);
                user.save();
                System.out.println("User is created"+ user.getUserId());
                DB_AuthenticationOTP.deleteOtp(otp);
                return new VerifyOtpOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE),user.getUserId());
            }
            DB_AuthenticationOTP.deleteOtp(otp);
            return new VerifyOtpOutput(new Response(Constants.SUCCESS_RESPONSE_CODE,Constants.SUCCESS_RESPONSE_MESSAGE), dbUser.getUserId());
        }
        else{
            return new VerifyOtpOutput(new Response(Constants.INVALID_INPUTS_RESPONSE_CODE,Constants.INVALID_INPUTS_RESPONSE_MESSAGE),null);
        }
    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class VerifyOtpOutput {
        public Response response;
        public String userId;

    }

    @Setter@Getter@NoArgsConstructor@AllArgsConstructor
    public static class VerifyOtpInput {
        public String mobileNo;
        public String otp;
        public String restaurantId;
        public String tableNo;
    }

    @Getter@Setter@NoArgsConstructor@AllArgsConstructor
    public static class Response {
        int responseCode;
        String message;
    }
}
